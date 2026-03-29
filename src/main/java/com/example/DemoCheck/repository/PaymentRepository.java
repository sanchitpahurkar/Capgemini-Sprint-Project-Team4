package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Payment;
import com.example.DemoCheck.entity.PaymentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "payment")
public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {
    @RestResource(path = "by-customer")
    Page<Payment> findByCustomerCustomerNumber(
            @Param("customerNumber") Integer customerNumber,
            Pageable pageable
    );
}