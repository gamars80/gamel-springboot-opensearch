package com.example.gamelspringbootopensearch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_hashtag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 pk

    @Column(nullable = false)
    private String tagName; // 태그명

}