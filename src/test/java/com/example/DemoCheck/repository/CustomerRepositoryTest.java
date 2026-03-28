package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    //Helper method
    private Customer createCustomer(int id, String name, String city) {
        Customer c = new Customer();
        c.setCustomerNumber(id);
        c.setCustomerName(name);
        c.setContactLastName("Last");
        c.setContactFirstName("First");
        c.setPhone("1234567890");
        c.setAddressLine1("Addr1");
        c.setCity(city);
        c.setState("MH");
        c.setPostalCode("440001");
        c.setCountry("India");
        c.setCreditLimit(new BigDecimal("10000"));
        return c;
    }

    //Basic insert + findAll
    @Test
    void testFindAllCustomers() {
        int initialSize = customerRepository.findAll().size();
        int baseId = initialSize + 1000;

        Customer c1 = createCustomer(baseId, "ABC Corp", "Mumbai");
        Customer c2 = createCustomer(baseId + 1, "XYZ Ltd", "Delhi");

        customerRepository.saveAll(List.of(c1, c2));

        List<Customer> customers = customerRepository.findAll();

        assertThat(customers.size()).isGreaterThanOrEqualTo(initialSize + 2);

        assertThat(customers)
                .extracting(Customer::getCustomerName)
                .contains("ABC Corp", "XYZ Ltd");
    }

    //Keyword search (name)
    @Test
    void testFindCustomersByKeyword() {
        int baseId = customerRepository.findAll().size() + 2000;

        customerRepository.save(createCustomer(baseId, "Tech Corp", "Mumbai"));

        var result = customerRepository.findCustomers("Tech", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Customer::getCustomerName)
                .contains("Tech Corp");
    }

    //Case insensitive search
    @Test
    void testFindCustomersCaseInsensitive() {
        int baseId = customerRepository.findAll().size() + 3000;

        customerRepository.save(createCustomer(baseId, "Global Tech", "Mumbai"));

        var result = customerRepository.findCustomers("tech", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Customer::getCustomerName)
                .contains("Global Tech");
    }

    //Multi-field search (city + country)
    @Test
    void testFindCustomersByCityAndCountry() {
        int baseId = customerRepository.findAll().size() + 4000;

        Customer c = createCustomer(baseId, "Alpha Corp", "Nagpur");
        c.setCountry("India");

        customerRepository.save(c);

        var byCity = customerRepository.findCustomers("Nagpur", PageRequest.of(0, 10));
        var byCountry = customerRepository.findCustomers("India", PageRequest.of(0, 10));

        assertThat(byCity.getContent())
                .extracting(Customer::getCity)
                .contains("Nagpur");

        assertThat(byCountry.getContent())
                .extracting(Customer::getCountry)
                .contains("India");
    }

    //Partial match test
    @Test
    void testPartialKeywordMatch() {
        int baseId = customerRepository.findAll().size() + 5000;

        customerRepository.save(createCustomer(baseId, "SuperTech Solutions", "City"));

        var result = customerRepository.findCustomers("Tech", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Customer::getCustomerName)
                .contains("SuperTech Solutions");
    }

    //Multiple matches
    @Test
    void testMultipleMatchesReturned() {
        int baseId = customerRepository.findAll().size() + 6000;

        customerRepository.save(createCustomer(baseId, "Tech A", "City"));
        customerRepository.save(createCustomer(baseId + 1, "Tech B", "City"));

        var result = customerRepository.findCustomers("Tech", PageRequest.of(0, 10));

        assertThat(result.getContent().size()).isGreaterThanOrEqualTo(2);
    }

    //Pagination basic
    @Test
    void testPaginationWorks() {
        int baseId = customerRepository.findAll().size() + 7000;

        for (int i = 0; i < 10; i++) {
            customerRepository.save(createCustomer(baseId + i, "Customer " + i, "City"));
        }

        int pageSize = 5;

        var page = customerRepository.findAll(PageRequest.of(0, pageSize));

        assertThat(page.getContent().size()).isEqualTo(pageSize);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(10);
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(2);
    }

    //Pagination metadata
    @Test
    void testPaginationMetadata() {
        int baseId = customerRepository.findAll().size() + 8000;

        for (int i = 0; i < 8; i++) {
            customerRepository.save(createCustomer(baseId + i, "Meta " + i, "City"));
        }

        int pageSize = 4;

        var page = customerRepository.findAll(PageRequest.of(0, pageSize));

        assertThat(page.getSize()).isEqualTo(pageSize);
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(2);
    }

    //Empty result
    @Test
    void testFindCustomersReturnsEmptyWhenNoMatch() {
        var result = customerRepository.findCustomers("NON_EXISTENT", PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    //Out of bounds page
    @Test
    void testPageOutOfBoundsReturnsEmpty() {
        var page = customerRepository.findAll(PageRequest.of(999, 5));

        assertThat(page.getContent()).isEmpty();
    }

    //Search + pagination combined
    @Test
    void testSearchWithPaginationLimit() {
        int baseId = customerRepository.findAll().size() + 10000;

        for (int i = 0; i < 10; i++) {
            customerRepository.save(createCustomer(baseId + i, "Tech " + i, "City"));
        }

        var page = customerRepository.findCustomers("Tech", PageRequest.of(0, 5));

        assertThat(page.getContent().size()).isEqualTo(5);
    }
}