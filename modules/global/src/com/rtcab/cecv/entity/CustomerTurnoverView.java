package com.rtcab.cecv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.DesignSupport;

import javax.persistence.*;

@NamePattern("%s|totalTurnover")
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