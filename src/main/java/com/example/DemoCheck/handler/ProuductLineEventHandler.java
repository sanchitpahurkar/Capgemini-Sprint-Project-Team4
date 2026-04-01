package com.example.DemoCheck.handler;

import com.example.DemoCheck.entity.Product;
import com.example.DemoCheck.entity.ProductLine;
import com.example.DemoCheck.repository.ProductLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(ProductLine.class)

public class ProuductLineEventHandler {
    @Autowired
    private ProductLineRepository productLineRepository;
    @HandleBeforeCreate
    public void handleBeforeCreate(ProductLine p) {
        if (productLineRepository.existsById(p.getProductLine())) {
            throw new IllegalArgumentException("Product already exists");
        }
    }
}
