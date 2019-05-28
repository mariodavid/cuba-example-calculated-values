package com.rtcab.cecv.web.screens.customer;

import com.haulmont.cuba.gui.screen.*;
import com.rtcab.cecv.entity.Customer;

@UiController("cecv_Customer.edit")
@UiDescriptor("customer-edit.xml")
@EditedEntityContainer("customerDc")
@LoadDataBeforeShow
public class CustomerEdit extends StandardEditor<Customer> {


    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Customer customer = getEditedEntity();

        Double totalTurnover = customer.calculateTotalTurnover();

        customer.setPersistedTotalTurnover(totalTurnover);
    }
    
    
}