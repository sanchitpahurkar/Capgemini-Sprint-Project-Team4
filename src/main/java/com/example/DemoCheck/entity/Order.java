package com.example.DemoCheck.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)  // THIS IS MISSING
    private Integer orderNumber;

    private LocalDate orderDate;
    private LocalDate requiredDate;
    private LocalDate shippedDate;

    private String status;
    private String comments;

    @ManyToOne
    @JoinColumn(name = "customerNumber")
//    @JsonIgnore   // ADD THIS
    private Customer customer;

    @OneToMany(mappedBy = "order")
//    @JsonIgnore   // ADD THIS
    List<OrderDetails> orderDetails;
}


