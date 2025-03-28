package com.example.gamelspringbootopensearch.repository;

import com.example.gamelspringbootopensearch.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}