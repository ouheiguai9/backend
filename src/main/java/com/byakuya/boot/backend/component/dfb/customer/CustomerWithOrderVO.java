package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.dfb.order.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2023/3/12 21:26
 */
public class CustomerWithOrderVO {
    @JsonIgnore
    private final Customer customer;
    @JsonIgnore
    private final Order order;

    public CustomerWithOrderVO(Customer customer, Order order) {
        this.customer = customer;
        this.order = order;
    }

    public String getPhone() {
        return customer.getPhone();
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long getId() {
        return customer.getId();
    }

    public LocalDateTime getCreateTime() {
        return customer.getUser().getAccount().getCreateTime();
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long getLastOrderId() {
        return order == null ? null : order.getId();
    }

    public LocalDateTime getLastOrderTime() {
        return order == null ? null : order.getCreateTime();
    }

    public BigDecimal getLastOrderFee() {
        return order == null ? null : order.getFee();
    }

    public String getLastOrderState() {
        return order == null ? null : order.getStateText();
    }
}
