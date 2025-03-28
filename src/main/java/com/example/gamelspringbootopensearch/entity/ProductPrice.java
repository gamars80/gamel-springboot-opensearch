package com.example.gamelspringbootopensearch.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 pk

    @Column(nullable = false)
    private BigDecimal price; // 상품가격

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // getters, setters
}