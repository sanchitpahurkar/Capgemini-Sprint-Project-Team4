package com.example.DemoCheck.projection;

import java.math.BigDecimal;

import org.springframework.data.rest.core.config.Projection;

import com.example.DemoCheck.entity.OrderDetails;

@Projection(name = "orderDetailView", types = { OrderDetails.class })
public interface OrderDetailProjection {

    Integer getOrderNumber();   // from OrderDetailId

    Integer getQuantityOrdered();

    BigDecimal getPriceEach();

    // Derived field
    default BigDecimal getTotalPrice() {
        return getPriceEach().multiply(BigDecimal.valueOf(getQuantityOrdered()));
    }

    // Nested access
    ProductInfo getProduct();

    interface ProductInfo {
        String getProductName();
    }
}
