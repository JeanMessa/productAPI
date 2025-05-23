package com.example.product.controller;

import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductRequestDTO;
import com.example.product.domain.product.ProductResponseDTO;
import com.example.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> get(@PathVariable UUID productId){
        ProductResponseDTO product = productService.getProduct(productId);
        return  ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> update(@PathVariable UUID productId, @RequestBody(required = false) ProductRequestDTO data){
        Product product = productService.updateProduct(productId,data);
        return ResponseEntity.ok(product);
    }


}
