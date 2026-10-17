package com.shopeasy.ordermanagement.service;

import com.shopeasy.ordermanagement.entity.Product;

import java.util.List;

public interface ProductService {

    Product addProduct(Product product);

    List<Product> getAllProducts();

    Product getProduct(Long id);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

}
