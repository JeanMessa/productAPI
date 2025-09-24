package com.example.product.repository;

import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p " +
            "WHERE (:name IS NULL OR LOWER(name) LIKE '%' || LOWER(CAST(:name AS string)) || '%') AND " +
            "((:minPrice IS NULL OR :minPrice <= price) AND (:maxPrice IS NULL OR :maxPrice >= price)) Order by p.name")
    public List<ProductResponseDTO> getAllFiltered(@Param("name") String name,
                                                   @Param("minPrice")  Double minPrice,
                                                   @Param("maxPrice")  Double maxPrice);
}
