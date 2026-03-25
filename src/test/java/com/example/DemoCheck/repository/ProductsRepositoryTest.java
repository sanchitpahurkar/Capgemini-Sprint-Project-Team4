package com.example.DemoCheck.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import com.example.DemoCheck.entity.Products;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductsRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSavedProduct() {
        Products p = new Products();

        p.setProductCode("S10_89876");
        p.setProductName("Parker pen");
        p.setProductLine("Pen");
        p.setProductVendor("AutoArt");
        p.setQuantityInStock(10);
        p.setBuyPrice(60);
        p.setMSRP(65);

        productRepository.save(p);
        // productRepository.flush(); // blocks LAZY write by hibernate

        Products saved = productRepository.findById("S10_89876").get();

        assertNotNull(saved);
        assertEquals("S10_89876", saved.getProductCode());
        assertEquals("Parker pen", saved.getProductName());
        assertEquals("Pen", saved.getProductLine());
        assertEquals("AutoArt", saved.getProductVendor());
        assertEquals(10, saved.getQuantityInStock());
        assertEquals(60, saved.getBuyPrice());
        assertEquals(65, saved.getMSRP());
    }
}
