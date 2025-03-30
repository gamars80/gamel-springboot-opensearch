package com.example.gamelspringbootopensearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRegistrationRequest {
    private String name;
    private String description;
    private Long categoryId;
    private Double price;
    private List<Long> hashtagIds; // 매핑할 해시태그 ID 목록
}