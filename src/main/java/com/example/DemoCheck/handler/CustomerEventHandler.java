package com.example.DemoCheck.handler;

import com.example.DemoCheck.entity.Customer;
import com.example.DemoCheck.repository.CustomerRepository;
import com.example.DemoCheck.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RepositoryEventHandler(Customer.class)
public class CustomerEventHandler {

    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private static final String POSTAL_REGEX = "^[0-9]{6}$";
    private static final int MAX_NAME_LENGTH = 50;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    //CREATE → check duplicate + validate
    @HandleBeforeCreate
    public void beforeCreate(Customer customer) {
        checkDuplicate(customer);
        validate(customer);
    }

    //UPDATE → only validate (no duplicate check)
    @HandleBeforeSave
    public void beforeSave(Customer customer) {
        validate(customer);
    }

    //Duplicate check ONLY for create
    private void checkDuplicate(Customer customer) {
        if (customer.getCustomerNumber() != null &&
                customerRepository.existsById(customer.getCustomerNumber())) {

            throw new IllegalArgumentException(
                    "Customer already exists with id: " + customer.getCustomerNumber()
            );
        }
    }

    //Main validation entry
    private void validate(Customer customer) {
        validateCustomerNumber(customer);
        validateCustomerName(customer);
        validateContactNames(customer);
        validatePhone(customer);
        validateAddress(customer);
        validateCity(customer);
        validatePostalCode(customer);
        validateCountry(customer);
        validateCreditLimit(customer);
        validateEmployee(customer);
    }

    //Individual validators

    private void validateCustomerNumber(Customer customer) {
        Integer id = customer.getCustomerNumber();

        if (id == null) {
            throw new IllegalArgumentException("customerNumber cannot be null");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("customerNumber must be a positive number");
        }
    }

    private void validateCustomerName(Customer customer) {
        String name = normalize(customer.getCustomerName(), "customerName");

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("customerName must be at most 50 characters");
        }

        customer.setCustomerName(name);
    }

    private void validateContactNames(Customer customer) {
        customer.setContactFirstName(
                normalize(customer.getContactFirstName(), "contactFirstName")
        );

        customer.setContactLastName(
                normalize(customer.getContactLastName(), "contactLastName")
        );
    }

    private void validatePhone(Customer customer) {
        String phone = customer.getPhone();

        if (phone == null || !phone.matches(PHONE_REGEX)) {
            throw new IllegalArgumentException("phone must be exactly 10 digits");
        }
    }

    private void validateAddress(Customer customer) {
        customer.setAddressLine1(
                normalize(customer.getAddressLine1(), "addressLine1")
        );

        // addressLine2 optional → trim if present
        if (customer.getAddressLine2() != null) {
            customer.setAddressLine2(customer.getAddressLine2().trim());
        }
    }

    private void validateCity(Customer customer) {
        customer.setCity(normalize(customer.getCity(), "city"));
    }

    private void validatePostalCode(Customer customer) {
        String postalCode = customer.getPostalCode();

        if (postalCode != null && !postalCode.matches(POSTAL_REGEX)) {
            throw new IllegalArgumentException("postalCode must be 6 digits");
        }
    }

    private void validateCountry(Customer customer) {
        customer.setCountry(normalize(customer.getCountry(), "country"));
    }

    private void validateCreditLimit(Customer customer) {
        BigDecimal credit = customer.getCreditLimit();

        if (credit != null && credit.signum() < 0) {
            throw new IllegalArgumentException("creditLimit cannot be negative");
        }
    }

    private void validateEmployee(Customer customer) {
        if (customer.getSalesRepEmployee() != null) {
            Integer empId = customer.getSalesRepEmployee().getEmployeeNumber();

            if (empId == null || !employeeRepository.existsById(empId)) {
                throw new IllegalArgumentException(
                        "Invalid or non-existing employee reference"
                );
            }
        }
    }

    //Utility method for common validation
    private String normalize(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}