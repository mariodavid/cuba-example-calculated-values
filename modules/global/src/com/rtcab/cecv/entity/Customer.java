package com.rtcab.cecv.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "CECV_CUSTOMER")
@Entity(name = "cecv_Customer")
public class Customer extends StandardEntity {
    private static final long serialVersionUID = 1195909453374797608L;

    @Column(name = "NAME")
    protected String name;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "customer")
    protected List<Order> orders;

    @Column(name = "PERSISTED_TOTAL_TURNOVER")
    protected Double persistedTotalTurnover;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customer")
    protected CustomerTurnoverView customerTotalTurnover;

    public Double getPersistedTotalTurnover() {
        return persistedTotalTurnover;
    }

    public void setPersistedTotalTurnover(Double persistedTotalTurnover) {
        this.persistedTotalTurnover = persistedTotalTurnover;
    }

    public CustomerTurnoverView getCustomerTotalTurnover() {
        return customerTotalTurnover;
    }

    public void setCustomerTotalTurnover(CustomerTurnoverView customerTotalTurnover) {
        this.customerTotalTurnover = customerTotalTurnover;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double calculateTotalTurnover() {

        if (getOrders() == null) {
            return 0d;
        }
        return getOrders()
                .stream()
                .map(Order::getOrderAmount)
                .reduce((a, b) -> a + b)
                .orElse(0d);

    }
}