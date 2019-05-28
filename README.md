# CUBA example - calculated values

In this example different options are shown to work with calculated entity values in entity lists.

## Example Domain

The example domains is order management, where the `Order` entity has an attribute `totalAmount`, which represents
value of the order. Now the requirement is, that it should be possible to aggregate (sum) all orders for a given
customer and display this sum in the list of customers accordingly.


There are several solutions to this problem, where each solution has its own pros and cons. Let's go over those those
options one-by-one.


## Solution 1: Generated Column

The first option to display the total turnover of a customer in the customer browse screen is to add a generated column
to the customers table. The generated column calculates the total turnover at the time where the table is displayed on the fly.

![solution-1](https://github.com/mariodavid/cuba-example-calculated-values/blob/master/img/solution-1.png)

### Implementation

The following two steps are necessary to create the generated column

#### Add column id to the table XML descriptor
```xml
<groupTable id="customersTable"
                    width="100%"
                    dataContainer="customersDc">
<!-- ... --->
<columns>
    <column id="name"/>
    <column id="totalTurnover" />
</columns>
```

The column id `totalTurnover` represents the generated column. It is not associated to a particular entity attribute.

INFO: However, in order to calculate the total turnover, the order entities have to loaded in the customer browse screen as well.
Therefore the `customer-view`, that is used in the customer browse screen loads all orders as well:

```xml
<view entity="cecv_Customer" name="customer-view" extends="_local">
    <property name="orders" view="_local"/>
</view>
```

#### Register Column Generator to table instance

The second step is to register a column generator based on the id defined in the XML descriptor above:

```java
public class CustomerBrowse extends StandardLookup<Customer> {

    @Inject
    protected GroupTable<Customer> customersTable;

    @Inject
    protected UiComponents uiComponents;

    @Subscribe
    protected void onInit(InitEvent event) {
        customersTable.addGeneratedColumn("totalTurnover", new Table.PrintableColumnGenerator<Customer, String>() {
            @Override
            public String getValue(Customer item) {
                return "" + item.calculateTotalTurnover();
            }

            @Override
            public Component generateCell(Customer entity) {
                Label label = uiComponents.create(Label.class);
                label.setValue(entity.calculateTotalTurnover());
                return label;
            }
        });
    }


}
```

Registering this column can happen via subscribing to the `InitEvent` of the browse screen.

Depending on the needs, it is either possible to register a `ColumnGenerator` (only UI table), `Printable` (only excel action)
 or both via `PrintableColumnGenerator` (as shown in the example).

The method `generateCell` defines the output for the UI table. In case a Text should be displayed it should be wrapped
into a Label component.

The method `getValue` determines the value for the excel column used by the excel export functionality of CUBA.

Within those methods the total turnover can be calculated. In this case the actual aggregation is delegated to the `Customer`
class itself.


### Summary

The main problem with this approach is that is removes the ability to filter / sort for this column. The reason
is that the filter possibility works directly on the JPA layer and delegates the operation to the database.

However, since the value is computed within the application, the database simply does not now about this value
and therefore is not able to filter for it. The same restriction is valid for sorting, since it is also done
on the database level.

#### Pros:
* arbitrary code can be executed
* everything can be rendered (Icons, Text, UI components etc.)

#### Cons:
* requires to load all associated data, that the calculation is based on
* Filtering is not possible
* Sorting is not possible


## Solution 2: Persisted Attribute

The second option that will enable to calculate and display the total turnover for each
customer as well. In this case the value is calculated at the point where the data is stored
and it will be stored within the customer entity as another column.

This way, the database knows about the values and due to this can filter & sort over this
attribute just as for any other attribute.

![solution-2](https://github.com/mariodavid/cuba-example-calculated-values/blob/master/img/solution-2.png)

### Implementation

#### Creating persisted attribute

The Customer entity gets a new attribute `persistedTotalTurnover`, which will hold the calculated
value:

```java
@Table(name = "CECV_CUSTOMER")
@Entity(name = "cecv_Customer")
public class Customer extends StandardEntity {

    // ...

    @Column(name = "PERSISTET_TOTAL_TURNOVER")
    protected Double persistetTotalTurnover;

    public Double getPersistetTotalTurnover() {
        return persistetTotalTurnover;
    }

    public void setPersistetTotalTurnover(Double persistetTotalTurnover) {
        this.persistetTotalTurnover = persistetTotalTurnover;
    }

}
```

#### Execute calculation at creation time

The value is filled at the point where the data storage happens. In this case it is done
directly within the `CustomerEdit` editor right before the save operation happens:

```
package com.rtcab.cecv.web.screens.customer;

import com.haulmont.cuba.gui.screen.*;
import com.rtcab.cecv.entity.Customer;
import com.rtcab.cecv.entity.Order;

@UiController("cecv_Customer.edit")
@UiDescriptor("customer-edit.xml")
@EditedEntityContainer("customerDc")
@LoadDataBeforeShow
public class CustomerEdit extends StandardEditor<Customer> {


    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Customer customer = getEditedEntity();

        Double totalTurnover = customer.calculateTotalTurnover();

        customer.setPersistetTotalTurnover(totalTurnover);
    }


}
```

INFO: be aware that there are multiple points in the lifecycle of persisting the data.
Depending on the needs of the application, the editor (`BeforeCommitChangesEvent`) might not
be the best option. In case the REST API is used e.g. the calculation of the total turnover
has to be calculated directly in the persistence layer, not in the UI layer.


### Summary

With storing the value before hand it is possible to leverage all standard functionalities of the platform.
Compared to the first solution, there are no constraints anymore, because the attribute is just another attribute
as any other attribute in the customer entity.

#### Pros:
* filtering is possible
* sorting is possible
* other platform features are also usable (e.g security constraints based on this calculated value)

#### Cons:
* data duplication



## Solution 3: SQL View

The third solution is a solves the problems of the first and the second solution. In this case a SQL View
is defined, that will to the aggregation. Then this view is mapped to an JPA entity, which will be linked
into to the customer.

This way, the database still knows about the data, therefore it is possible to filter / sort for the column.
However, the data is not persisted, but calculated on the fly during fetching the data from the view.

With JPA is possible to map an entity against a database view instead of a regular table, which is crucial
to the CUBA application, since mostly all functionalities are based on this fundamental building block.

### Implementation

#### SQL View creation

The first step is to define the SQL view and update the database accordingly. The `30.create-db.sql` as well
as the `190528-4-createCustomerTurnoverView.sql` contain the view definition:

```sql
create view CECV_CUSTOMER_TURNOVER_VIEW as
select
sum(o.order_amount) TOTAL_TURNOVER,
c.id ID,
c.id CUSTOMER_ID,
c.VERSION,
c.CREATE_TS,
c.CREATED_BY,
c.UPDATE_TS,
c.UPDATED_BY,
c.DELETE_TS,
c.DELETED_BY
from   CECV_CUSTOMER c
join   CECV_ORDER o
on     c.id = o.customer_id
group by c.id;
```

Here it is important to define the columns that should be available for the CUBA JPA entity. The view contains
all the infrastructure attributes (`ID`, `VERSION`, `DELETE_TS`) and re-uses them directly from the customer.

Then the aggregation is calculated via the `TOTAL_TURNOVER` column.

#### CustomerTurnoverView Entity

The second step is to create an entity and map it to the view:

```
@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "CECV_CUSTOMER_TURNOVER_VIEW")
@Entity(name = "cecv_CustomerTurnoverView")
public class CustomerTurnoverView extends StandardEntity {
    private static final long serialVersionUID = -6969369474397318400L;

    @Column(name = "TOTAL_TURNOVER")
    protected Double totalTurnover;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getTotalTurnover() {
        return totalTurnover;
    }

    public void setTotalTurnover(Double totalTurnover) {
        this.totalTurnover = totalTurnover;
    }
}
```

On the Customer entity, there will also be a connection to `CustomerTurnoverView` as a One-to-One connection:

```
@OneToOne(fetch = FetchType.LAZY, mappedBy = "customer")
protected CustomerTurnoverView customerTotalTurnover;
```

#### Show totalTurnover attribute in UI

With that connection in place, it is possible to show the `totalTurnover` attribute through the `customerTotalTurnover`
association in the UI:

```xml
<groupTable id="customersTable"
                    width="100%"
                    dataContainer="customersDc">
<!-- ... --->
<columns>
    <column id="name"/>
    <!-- ... --->
    <column id="customerTotalTurnover.totalTurnover" />
</columns>
```



### Summary

This solution still keeps the aggregation logic in the database. Therefore sorting / filtering is possible.
Compared to solution 2, it does not require to duplicate data. This is the main benefit over the persisted attribute.
The trade-off here is that it has to be calculated on the fly, which basically boils down to performance. This could furthermore
be solved by a materialized view e.g.

#### Pros:
* filtering is possible
* sorting is possible
* other platform features are also usable (e.g security constraints based on this calculated value)
* no data duplication

#### Cons:
* only limited to the aggregation possibilities of the SQL database
* Views require a little more manual mapping against the JPA entity