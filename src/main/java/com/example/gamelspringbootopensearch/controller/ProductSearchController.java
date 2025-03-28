package com.example.gamelspringbootopensearch.controller;


import com.example.gamelspringbootopensearch.dto.ProductDTO;
import com.example.gamelspringbootopensearch.service.ProductSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    // 예시: /api/products/search?categoryId=1&hashtagIds=1,2,3
    @GetMapping("/api/products/search")
    public List<ProductDTO> searchProducts(
            @RequestParam Long categoryId,
            @RequestParam String hashtagIds
    ) throws IOException {
        List<Long> tags = Arrays.stream(hashtagIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return productSearchService.searchProducts(categoryId, tags);
    }
}