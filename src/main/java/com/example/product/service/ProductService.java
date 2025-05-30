package com.example.product.service;

import com.example.product.domain.product.DeleteResponseDTO;
import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductRequestDTO;
import com.example.product.domain.product.ProductResponseDTO;
import com.example.product.domain.user.RegisterResponseDTO;
import com.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public List<ProductResponseDTO> getAllProducts(String name,Double minPrice,Double maxPrice){
        List<ProductResponseDTO> allProducts = productRepository.getAllFiltered(name,minPrice,maxPrice);
        return allProducts;
    }

    public ProductResponseDTO getProduct(UUID productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalMonitorStateException("Product not found."));
        return new ProductResponseDTO(product);
    }

    public Product updateProduct(UUID productId, ProductRequestDTO data){
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalMonitorStateException("Product not found."));
        if (data.name()!=null) {
            product.setName(data.name());
        }
        if (data.price()!=null) {
            product.setPrice(data.price());
        }
        return productRepository.save(product);
    }

    public DeleteResponseDTO deleteProduct(UUID productId){
        if (productRepository.findById(productId).isPresent()){
            productRepository.deleteById(productId);
            return new DeleteResponseDTO(HttpStatus.OK,"Product " + productId + " deleted.");
        }else {
            return new DeleteResponseDTO(HttpStatus.BAD_REQUEST,"Product not found.");
        }
    }
}
