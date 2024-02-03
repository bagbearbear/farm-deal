package com.farmdeal.domain.product.repository;


import com.farmdeal.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}

