package com.example.DemoCheck.projection;

import java.math.BigDecimal;

import org.springframework.data.rest.core.config.Projection;

import com.example.DemoCheck.entity.Product;

// custom view for Product table, hides table id
@Projection(name = "productView", types = Product.class)
public interface ProductProjection {
    String getProductName();
    String getProductLine();
    String getProductVendor();
    Integer getQuantityInStock();
    BigDecimal getBuyPrice();
    BigDecimal getMSRP();
}
