package com.example.DemoCheck.util;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.DemoCheck.repository.ProductRepository;


@Component
public class ProductCodeGenerator {

    @Autowired
    private ProductRepository productRepository;

    public String generateProductCode() {
        String code;
        int attempts = 0;

        do {
            long time = Instant.now().toEpochMilli();
            int random = ThreadLocalRandom.current().nextInt(1000, 9999);

            long combined = Math.abs((time + random) % 1_000_000);

            int prefix = (int)(combined / 10000);
            int suffix = (int)(combined % 10000);

            code = String.format("S%02d_%04d", prefix, suffix);

            attempts++;

        } while (productRepository.existsById(code) && attempts < 3);

        return code;
    }
}