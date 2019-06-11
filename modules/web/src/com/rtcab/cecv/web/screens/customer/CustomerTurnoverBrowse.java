package com.rtcab.cecv.web.screens.customer;

import com.haulmont.cuba.gui.screen.*;
import com.rtcab.cecv.entity.Customer;

@UiController("cecv_CustomerTurnover.browse")
@UiDescriptor("customer-turnover-browse.xml")
@LookupComponent("customersTable")
@LoadDataBeforeShow
public class CustomerTurnoverBrowse extends StandardLookup<Customer> {
}