package com.example.DemoCheck.projection;


import com.example.DemoCheck.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;




@Projection(name = "orderView", types = Order.class)
public interface OrderProjection {

    LocalDate getOrderDate();
    LocalDate getRequiredDate();
    LocalDate getShippedDate();
    String getStatus();
    String getComments();

    @Value("#{target.customer.customerNumber}")
    Integer getCustomerNumber();
}