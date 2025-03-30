package com.example.gamelspringbootopensearch.repository;

import com.example.gamelspringbootopensearch.entity.Product;
import com.example.gamelspringbootopensearch.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    ProductPrice findByProduct(Product product);
}
