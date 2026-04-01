package com.example.DemoCheck.projection;

import com.example.DemoCheck.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "orderListView", types = Order.class)
public interface OrderListProjection {

    LocalDate getOrderDate();
    LocalDate getRequiredDate();
    LocalDate getShippedDate();

    String getStatus();
    String getComments();
}