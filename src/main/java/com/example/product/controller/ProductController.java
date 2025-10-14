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
    public ResponseEntity<List<ProductResponseDTO>> getAll(@RequestParam(required = false) String name,
                                                           @RequestParam(required = false)  Double minPrice,
                                                           @RequestParam(required = false)  Double maxPrice){
        List<ProductResponseDTO> allProducts = productService.getAllProducts(name,minPrice,maxPrice);
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

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> delete(@PathVariable UUID productId){
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product " + productId + " deleted.");
    }

}
