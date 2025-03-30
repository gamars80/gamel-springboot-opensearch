package com.example.gamelspringbootopensearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateMessage {
    private Long productId;
    private String name;
    private Double price;
}