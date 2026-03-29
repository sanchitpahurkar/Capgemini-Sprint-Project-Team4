package com.example.DemoCheck.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PaymentId implements Serializable {

    @Column(name = "customerNumber")
    private Integer customerNumber;

    @Column(name = "checkNumber")
    private String checkNumber;
}