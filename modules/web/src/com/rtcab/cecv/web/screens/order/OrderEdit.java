package com.rtcab.cecv.web.screens.order;

import com.haulmont.cuba.gui.screen.*;
import com.rtcab.cecv.entity.Order;

@UiController("cecv_Order.edit")
@UiDescriptor("order-edit.xml")
@EditedEntityContainer("orderDc")
@LoadDataBeforeShow
public class OrderEdit extends StandardEditor<Order> {
}