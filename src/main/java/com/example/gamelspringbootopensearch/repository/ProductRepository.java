package com.example.gamelspringbootopensearch.repository;


import com.example.gamelspringbootopensearch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}