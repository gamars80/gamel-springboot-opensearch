package com.example.gamelspringbootopensearch.repository;

import com.example.gamelspringbootopensearch.entity.ProductCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryMappingRepository extends JpaRepository<ProductCategoryMapping, Long> {
}