package com.example.product.service;

import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductRequestDTO;
import com.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(ProductRequestDTO data){
        Product product = new Product();
        product.setName(data.name());
        product.setPrice(data.price());
        return productRepository.save(product);
    }
}
