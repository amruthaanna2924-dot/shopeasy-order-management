package com.shopeasy.ordermanagement.repository;


import com.shopeasy.ordermanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

}
