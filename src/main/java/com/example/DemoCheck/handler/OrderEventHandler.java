package com.example.DemoCheck.handler;

import com.example.DemoCheck.entity.Order;
import com.example.DemoCheck.repository.CustomerRepository;
import com.example.DemoCheck.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RepositoryEventHandler(Order.class)
public class OrderEventHandler {

    private static final int MAX_STATUS_LENGTH = 15;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // CREATE
    @HandleBeforeCreate
    public void beforeCreate(Order order) {
        validate(order);
    }

    // UPDATE
    @HandleBeforeSave
    public void beforeSave(Order order) {
        validate(order);
    }

    // Main validation
    private void validate(Order order) {
        validateOrderDate(order);
        validateRequiredDate(order);
        validateShippedDate(order);
        validateStatus(order);
        validateCustomer(order);
    }

    // Order Date validation
    private void validateOrderDate(Order order) {
        if (order.getOrderDate() == null) {
            throw new IllegalArgumentException("orderDate cannot be null");
        }
    }

    // Required Date validation
    private void validateRequiredDate(Order order) {
        if (order.getRequiredDate() == null) {
            throw new IllegalArgumentException("requiredDate cannot be null");
        }

        if (order.getOrderDate() != null &&
                order.getRequiredDate().isBefore(order.getOrderDate())) {

            throw new IllegalArgumentException(
                    "requiredDate must be after orderDate"
            );
        }
    }

    // Shipped Date validation
    private void validateShippedDate(Order order) {

        if (order.getShippedDate() != null &&
                order.getOrderDate() != null &&
                order.getShippedDate().isBefore(order.getOrderDate())) {

            throw new IllegalArgumentException(
                    "shippedDate cannot be before orderDate"
            );
        }
    }

    // Status validation
    private void validateStatus(Order order) {

        String status = order.getStatus();

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("status cannot be blank");
        }

        if (status.length() > MAX_STATUS_LENGTH) {
            throw new IllegalArgumentException(
                    "status must be less than 15 characters"
            );
        }

        order.setStatus(status.trim());
    }

    // Customer validation
    private void validateCustomer(Order order) {

        if (order.getCustomer() == null ||
                order.getCustomer().getCustomerNumber() == null) {

            throw new IllegalArgumentException(
                    "Customer is required for Order"
            );
        }

        Integer customerId =
                order.getCustomer().getCustomerNumber();

        if (!customerRepository.existsById(customerId)) {

            throw new IllegalArgumentException(
                    "Invalid customer reference: " + customerId
            );
        }
    }
}
