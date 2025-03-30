package com.example.gamelspringbootopensearch.controller;


import com.example.gamelspringbootopensearch.dto.ProductDTO;
import com.example.gamelspringbootopensearch.dto.ProductRegistrationRequest;
import com.example.gamelspringbootopensearch.dto.ProductUpdateRequest;
import com.example.gamelspringbootopensearch.entity.Product;
import com.example.gamelspringbootopensearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 예시: /api/products/search?categoryId=1&hashtagIds=1,2,3
    @GetMapping("/search")
    public List<ProductDTO> searchProducts(
            @RequestParam Long categoryId,
            @RequestParam String hashtagIds
    ) throws IOException {
        List<Long> tags = Arrays.stream(hashtagIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return productService.searchProducts(categoryId, tags);
    }

    @PostMapping("/register")
    public ResponseEntity<Product> registerProduct(@RequestBody ProductRegistrationRequest request) {
        Product savedProduct = productService.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable("id") Long productId,
            @RequestBody ProductUpdateRequest updateRequest) {
        productService.updateProduct(productId, updateRequest);
        return ResponseEntity.ok("ok");
    }
}