package com.example.gamelspringbootopensearch.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_price")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 pk

    @Column(nullable = false)
    private Double price; // 상품가격

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void updateProductPrice(Double price) {
        this.price = price;
    }
}