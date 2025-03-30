package com.example.gamelspringbootopensearch.dto;

import lombok.Data;

@Data
public class ProductUpdateRequest {
    private String name;
    private Double price;
}