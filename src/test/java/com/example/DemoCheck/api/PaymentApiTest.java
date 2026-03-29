package com.example.DemoCheck.api;

import com.example.DemoCheck.entity.Customer;
import com.example.DemoCheck.entity.Payment;
import com.example.DemoCheck.entity.PaymentId;
import com.example.DemoCheck.repository.CustomerRepository;
import com.example.DemoCheck.repository.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // ---------------- HELPER METHODS ----------------

    private int generateId() {
        return (int) (System.nanoTime() % 1000000) + 1000000;
    }

    private Customer createCustomer(int id) {
        Customer c = new Customer();
        c.setCustomerNumber(id);
        c.setCustomerName("Test Customer");
        c.setContactLastName("Doe");
        c.setContactFirstName("John");
        c.setPhone("1234567890");
        c.setAddressLine1("Addr1");
        c.setCity("Nagpur");
        c.setCountry("India");
        c.setCreditLimit(new BigDecimal("10000"));
        return customerRepository.save(c);
    }

    private Payment createPayment(int customerId, String checkNumber) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        Payment p = new Payment();
        p.setId(new PaymentId(customerId, checkNumber));
        p.setCustomer(customer);
        p.setPaymentDate(LocalDate.now());
        p.setAmount(new BigDecimal("5000.00"));

        return paymentRepository.save(p);
    }

    // ---------------- GET ALL ----------------

    @Test
    void testGetAllPayments() throws Exception {

        int customerId = generateId();
        createCustomer(customerId);
        createPayment(customerId, "CHK1");

        mockMvc.perform(get("/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isArray());
    }

    // ---------------- PAGINATION ----------------

    @Test
    void testPagination() throws Exception {

        mockMvc.perform(get("/payment")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.totalElements").exists());
    }

    @Test
    void testPaginationOutOfBounds() throws Exception {

        mockMvc.perform(get("/payment")
                        .param("page", "999")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isEmpty());
    }

    // ---------------- FILTER BY CUSTOMER ----------------

    @Test
    void testGetPaymentsByCustomer() throws Exception {

        int customerId = generateId();
        createCustomer(customerId);

        createPayment(customerId, "CHK1");
        createPayment(customerId, "CHK2");

        mockMvc.perform(get("/payment/search/by-customer")
                        .param("customerNumber", String.valueOf(customerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isArray())
                .andExpect(jsonPath("$._embedded.payments.length()").value(2));
    }

    @Test
    void testGetPaymentsByCustomerEmpty() throws Exception {

        int customerId = generateId();
        createCustomer(customerId);

        mockMvc.perform(get("/payment/search/by-customer")
                        .param("customerNumber", String.valueOf(customerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isEmpty());
    }

    // ---------------- PROJECTION ----------------

    @Test
    void testProjection() throws Exception {

        int customerId = generateId();
        createCustomer(customerId);
        createPayment(customerId, "CHK1");

        mockMvc.perform(get("/payment")
                        .param("projection", "paymentView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isArray())
                .andExpect(jsonPath("$._embedded.payments[*].paymentDate").exists())
                .andExpect(jsonPath("$._embedded.payments[*].amount").exists());
    }

    // ---------------- CLEANUP ----------------

    @AfterEach
    void cleanup() {

        paymentRepository.findAll().stream()
                .filter(p -> p.getId().getCustomerNumber() >= 1000000)
                .forEach(paymentRepository::delete);

        customerRepository.findAll().stream()
                .filter(c -> c.getCustomerNumber() >= 1000000)
                .forEach(customerRepository::delete);
    }
}