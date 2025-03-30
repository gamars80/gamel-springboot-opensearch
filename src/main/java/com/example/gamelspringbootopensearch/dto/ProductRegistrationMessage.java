package com.example.gamelspringbootopensearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRegistrationMessage implements Serializable {
    private Long productId;
    private String name;
    private String description;
    private Long categoryId;       // 상품 카테고리 ID
    private Double price;
    private List<Long> hashtagIds; // 매핑할 해시태그 ID 목록
}