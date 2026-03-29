package com.example.DemoCheck.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @EmbeddedId
    private PaymentId id;

    @Column(name = "paymentDate", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    // Relationship → Many Payments belong to one Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerNumber") // maps FK to composite key
    @JoinColumn(name = "customerNumber", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;
}