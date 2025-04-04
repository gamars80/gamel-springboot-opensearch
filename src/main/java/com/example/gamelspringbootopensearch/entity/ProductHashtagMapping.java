package com.example.gamelspringbootopensearch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_hashtag_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductHashtagMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 pk

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "hashtag_id", nullable = false)
    private ProductHashtag hashtag;

    // getters, setters
}