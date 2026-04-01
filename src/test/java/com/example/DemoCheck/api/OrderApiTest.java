package com.example.DemoCheck.api;

import com.example.DemoCheck.entity.Customer;
import com.example.DemoCheck.entity.Order;
import com.example.DemoCheck.repository.CustomerRepository;
import com.example.DemoCheck.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;


//    @Test
//    void testSaveOrder() throws Exception {
//
//        Order order = new Order();
//        order.setOrderNumber(10150);
//        order.setStatus("Processing");
//
//        mockMvc.perform(post("/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(order)))
//                .andExpect(status().isCreated());
//    }
//@Test
//void testSaveOrder() throws Exception {
//    String json = """
//        {
//            "orderNumber": 10150,
//            "status": "Processing"
//        }
//        """;
//
//    mockMvc.perform(post("/orders")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(json))
//            .andExpect(status().isCreated())
//            .andExpect(header().exists("Location"));
//}


    @Test
    void testGetOrderNotFound() throws Exception {

        mockMvc.perform(get("/orders/999999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testCreateOrder() throws Exception {
        // 1. Create a Customer to satisfy the Foreign Key requirement
        Customer customer = new Customer();
        customer.setCustomerNumber(119);
        customer.setCustomerName("Test Corp");
        customer.setContactFirstName("John");
        customer.setContactLastName("Doe");
        customer.setPhone("555-0123");
        customer.setAddressLine1("123 Java Lane");
        customer.setCity("Nagpur");
        customer.setCountry("India");
        customerRepository.save(customer);

        // 2. Include the "customer" link in your JSON using the URI format
        String json = """
    {
        "orderNumber": 10150,
        "orderDate": "2026-04-01",
        "requiredDate": "2026-04-10",
        "status": "Processing",
        "customer": "/customers/119"
    }
    """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
    @Test
    void testGetOrderById() throws Exception {

        mockMvc.perform(get("/orders/10150"))
                .andExpect(status().isOk());
    }
    @Test
    void testGetAllOrders() throws Exception {

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }
    @Test
    void testUpdateOrderUsingPatch() throws Exception {
        // We only send the field we want to change
        String json = """
    {
        "status": "Shipped"
    }
    """;

        // Change .put() to .patch()
        mockMvc.perform(patch("/orders/10150")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }
    @Test
    void testUpdateOrderUsingPut() throws Exception {
        // We only send the field we want to change
        String json = """
            {
              "orderNumber": 51,
              "orderDate": "2023-01-01",
              "requiredDate": "2023-01-10",
              "status": "Shipped",
              "customer": "/customers/141"
            }
            """;

        // Change .put() to .patch()
        mockMvc.perform(put("/orders/51")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }
    @Test
    void testDeleteOrder() throws Exception {

        mockMvc.perform(delete("/orders/10150"))
                .andExpect(status().isNoContent());
    }

    // Failer Test Cases

    @Test
    void testCreateOrderFail() throws Exception {

        String json = "{}";

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testGetInvalidId() throws Exception {

        mockMvc.perform(get("/orders/99999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetEmptyList() throws Exception {

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }
    @Test
    void testDeleteInvalid() throws Exception {

        mockMvc.perform(delete("/orders/99999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateInvalid() throws Exception {
        String json = """
    {
        "status": "Cancelled"
    }
    """;

        // Change .put() to .patch()
        mockMvc.perform(patch("/orders/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                // Expect 404 Not Found because order 99999 doesn't exist
                .andExpect(status().isNotFound());
    }
    @Test
    void testValidationFail() throws Exception {

        String json = """
    {
        "status": null
    }
    """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                // Change status().isInternalServerError() to status().isBadRequest()
                .andExpect(status().isBadRequest());
    }

    // for pagination
    @Test
    void testPagination() throws Exception {

        mockMvc.perform(get("/orders?page=0&size=5"))
                .andExpect(status().isOk());
    }
    @Test
    void createOrder_withValidCustomerNumber_shouldSucceed() throws Exception {
        // 1. Fill EVERY mandatory field to satisfy DB constraints
        Customer customer = new Customer();
        customer.setCustomerNumber(119);
        customer.setCustomerName("Test Corp");
        customer.setContactFirstName("John"); // REQUIRED
        customer.setContactLastName("Doe");   // LIKELY REQUIRED
        customer.setPhone("555-1234");        // LIKELY REQUIRED
        customer.setAddressLine1("123 Java Lane");
        customer.setCity("Nagpur");
        customer.setCountry("India");

        customerRepository.save(customer);

        String orderJson = """
        {
          "orderNumber": 1001,
          "orderDate": "2023-01-01",
          "requiredDate": "2023-01-10",
          "status": "In Process",
          "customer": "/customers/119"
        }
        """;

        // 2. Perform the POST
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated());
    }
    @Test
    void createOrder_withInvalidCustomerNumber_shouldFail() throws Exception {
        String orderJson = """
            {
              "orderNumber": 999,
              "orderDate": "2023-01-01",
              "requiredDate": "2023-01-10",
              "status": "In Process",
              "customer": "/customers/99999"
            }
            """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isBadRequest()); // Change from isInternalServerError
    }
}