package com.example.DemoCheck.projection;

import com.example.DemoCheck.entity.Payment;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.time.LocalDate;

@Projection(name = "paymentView", types = Payment.class)
public interface PaymentProjection {

    LocalDate getPaymentDate();

    BigDecimal getAmount();
}