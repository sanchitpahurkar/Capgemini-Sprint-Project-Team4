package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Office;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OfficeRepositoryTest {

    @Autowired
    private OfficeRepository officeRepository;

    private Office createOffice(
            String officeCode,
            String city,
            String phone,
            String addressLine1,
            String addressLine2,
            String state,
            String country,
            String postalCode,
            String territory
    ) {
        return Office.builder()
                .officeCode(officeCode)
                .city(city)
                .phone(phone)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .state(state)
                .country(country)
                .postalCode(postalCode)
                .territory(territory)
                .build();
    }

    @Test
    void findAll_shouldReturnData() {
        officeRepository.saveAll(List.of(
                createOffice("T01", "Paris", "1111111111", "A1", "A2", "State1", "France", "75001", "EMEA"),
                createOffice("T02", "London", "2222222222", "B1", "B2", "State2", "UK", "EC1A", "EMEA")
        ));
        List<Office> offices = officeRepository.findAll();

        assertNotNull(offices);
        assertFalse(offices.isEmpty());
    }

    @Test
    void findByCityIn_shouldReturnEmptyList_whenCitiesDoNotExist() {
        officeRepository.saveAll(List.of(
                createOffice("T21", "AlphaCity", "1111111111", "A1", "A2", "S1", "India", "440001", "APAC"),
                createOffice("T22", "BetaCity", "2222222222", "B1", "B2", "S2", "India", "440002", "APAC")
        ));

        List<Office> offices = officeRepository.findByCityIn(List.of("NoSuchCity1", "NoSuchCity2"));

        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    //add
    @Test
    void save_shouldInsertOffice_whenOfficeCodeIsNew(){
        Office office = createOffice("R91", "Nagpur", "9999999999",
                "Main Road", "Near Square", "MH", "India", "440001", "APAC");

        Office saved = officeRepository.save(office);

        assertNotNull(saved);
        assertEquals("R91",saved.getOfficeCode());
    }

    //update
    @Test
    void save_shouldUpdatePhone_whenOfficeExists(){
        officeRepository.save(
                createOffice("R93", "London", "2222222222", "B1", "B2", "S2", "UK", "EC1A", "EMEA")
        );

        Office office = officeRepository.findById("R93").orElseThrow();
        office.setPhone("7777777777");
        officeRepository.saveAndFlush(office);

        Office updated = officeRepository.findById("R93").orElseThrow();
        assertEquals("7777777777",updated.getPhone());
    }

    //full update
    @Test
    void save_shouldUpdateOffice_whenOfficeExists(){
        officeRepository.save(
                createOffice("R94", "Tokyo", "3333333333", "C1", "C2", "S3", "Japan", "100001", "APAC")
        );

        Office office = officeRepository.findById("R94").orElseThrow();
        office.setCity("Tokyo Updated");
        office.setPhone("6666666666");
        office.setAddressLine1("Updated Line 1");

        officeRepository.saveAndFlush(office);

        Office updated = officeRepository.findById("R94").orElseThrow();
        assertEquals("Tokyo Updated",updated.getCity());
        assertEquals("6666666666",updated.getPhone());
        assertEquals("Updated Line 1",updated.getAddressLine1());
    }

    @Test
    void save_shouldCreateOffice_whenOfficeDoesNotExist(){
        Office office = createOffice("R95", "Brand New City", "5555555555",
                "New 1", "New 2", "MH", "India", "440003", "APAC");

        officeRepository.saveAndFlush(office);

        assertTrue(officeRepository.existsById("R95"));
    }
}
