package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Office;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OfficeRepositoryTest {

    @Autowired
    private OfficeRepository officeRepository;

    @Test
    void findAll_shouldReturnData() {

        List<Office> offices = officeRepository.findAll();

        assertNotNull(offices);
        assertFalse(offices.isEmpty());
    }
}
