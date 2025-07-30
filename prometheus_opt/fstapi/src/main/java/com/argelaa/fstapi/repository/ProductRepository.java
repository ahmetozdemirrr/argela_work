package com.argelaa.fstapi.repository;

import com.argelaa.fstapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{

    List<Product> findByCategory(String category);
    List<Product> findByQuantityGreaterThan(Long quantity);
    List<Product> findByCategoryAndQuantityGreaterThan(String category, Long quantity);
    Optional<Product> findByName(String name);
}
