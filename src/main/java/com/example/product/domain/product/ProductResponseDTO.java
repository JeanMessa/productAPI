package com.example.product.domain.product;

import java.util.UUID;

public record ProductResponseDTO(UUID productId,String name, Double price) {
    public ProductResponseDTO(Product product) {
        this(product.getProductId(),product.getName(),product.getPrice());
    }
}
