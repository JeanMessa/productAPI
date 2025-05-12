package com.example.product.controller;

import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductRequestDTO;
import com.example.product.domain.product.ProductResponseDTO;
import com.example.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequestDTO data){
        Product newProduct = productService.createProduct(data);
        return ResponseEntity.ok(newProduct);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll(){
        List<ProductResponseDTO> allProducts = productService.getAllProducts();
        return  ResponseEntity.ok(allProducts);
    }
}
