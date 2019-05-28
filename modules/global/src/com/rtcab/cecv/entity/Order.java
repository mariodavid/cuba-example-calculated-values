package com.rtcab.cecv.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "CECV_ORDER")
@Entity(name = "cecv_Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 7120616751634348249L;

    @Column(name = "ORDER_DATE")
    protected LocalDate orderDate;

    @Column(name = "ORDER_AMOUNT")
    protected Double orderAmount;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
}