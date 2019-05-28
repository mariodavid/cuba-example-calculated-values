package com.rtcab.cecv.web.screens.customer;

import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.screen.*;
import com.rtcab.cecv.entity.Customer;

import javax.inject.Inject;

@UiController("cecv_Customer.browse")
@UiDescriptor("customer-browse.xml")
@LookupComponent("customersTable")
@LoadDataBeforeShow
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