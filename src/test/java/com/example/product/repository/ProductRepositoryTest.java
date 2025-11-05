package com.example.product.repository;

import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Nested
    class getAllFiltered{

        List<Product> createAndPersistProducts(){
            return new ArrayList<>(List.of(
                    testEntityManager.persistAndFlush(new Product(null,"Smartphone X",10.5)),
                    testEntityManager.persistAndFlush(new Product(null,"Smartphone Y",20.5)),
                    testEntityManager.persistAndFlush(new Product(null,"Notebook X",50.5)),
                    testEntityManager.persistAndFlush(new Product(null,"Notebook Y",100.6))
            ));
        }

        List<Product> getSortedListByName(List<Product> productList) {
            productList.sort(Comparator.comparing(Product::getName));
            return productList;
        }

        @DisplayName("Should return all products when is called without filters.")
        @Test
        void getAllFiltered_NoFilters_ReturnsAllProducts(){
            //ARRANGE
            List<Product> productList = createAndPersistProducts();
            productList = getSortedListByName(productList);

            //ACT
            List<ProductResponseDTO> result = productRepository.getAllFiltered(null,null,null);

            //ASSERT
            assertEquals(productList.size(), result.size());

            for (int i=0; i<result.size();i++){
                assertEquals(productList.get(i).getProductId(),result.get(i).productId());
                assertEquals(productList.get(i).getName(),result.get(i).name());
                assertEquals(productList.get(i).getPrice(),result.get(i).price());
            }
        }

        @DisplayName("Should return products filtered by name (case-insensitive substring).")
        @Test
        void getAllFiltered_WithNameFilter_ReturnsMatchingProducts(){
            //ARRANGE
            List<Product> productList = createAndPersistProducts();
            productList = getSortedListByName(productList);

            String nameFilter = "smartphone";

            productList.removeIf(product -> !product.getName().toLowerCase().contains(nameFilter.toLowerCase()));

            //ACT
            List<ProductResponseDTO> result = productRepository.getAllFiltered(nameFilter,null,null);

            //ASSERT
            assertEquals(productList.size(), result.size());

            for (int i=0; i<result.size();i++){
                assertEquals(productList.get(i).getProductId(),result.get(i).productId());
                assertEquals(productList.get(i).getName(),result.get(i).name());
                assertEquals(productList.get(i).getPrice(),result.get(i).price());
            }
        }

        @DisplayName("Should return products with price greater than or equal to min price.")
        @Test
        void getAllFiltered_WithMinPriceFilter_ReturnsAboveMinPriceProducts(){
            //ARRANGE
            List<Product> productList = createAndPersistProducts();
            productList = getSortedListByName(productList);

            Double minPriceFilter = 50.5;

            productList.removeIf(product -> product.getPrice() <  minPriceFilter);

            //ACT
            List<ProductResponseDTO> result = productRepository.getAllFiltered(null,minPriceFilter,null);

            //ASSERT
            assertEquals(productList.size(), result.size());

            for (int i=0; i<result.size();i++){
                assertEquals(productList.get(i).getProductId(),result.get(i).productId());
                assertEquals(productList.get(i).getName(),result.get(i).name());
                assertEquals(productList.get(i).getPrice(),result.get(i).price());
            }
        }

        @DisplayName("Should return products with price less than or equal to max price.")
        @Test
        void getAllFiltered_WithMaxPriceFilter_ReturnsBelowMaxPriceProducts(){
            //ARRANGE
            List<Product> productList = createAndPersistProducts();
            productList = getSortedListByName(productList);

            Double maxPriceFilter = 20.5;

            productList.removeIf(product -> product.getPrice() >  maxPriceFilter);

            //ACT
            List<ProductResponseDTO> result = productRepository.getAllFiltered(null,null,maxPriceFilter);

            //ASSERT
            assertEquals(productList.size(), result.size());

            for (int i=0; i<result.size();i++){
                assertEquals(productList.get(i).getProductId(),result.get(i).productId());
                assertEquals(productList.get(i).getName(),result.get(i).name());
                assertEquals(productList.get(i).getPrice(),result.get(i).price());
            }
        }

        @DisplayName("Should return products who match with all filters when is called with all filters.")
        @Test
        void getAllFiltered_WithAllFilters_ReturnsCombinedResultsProducts(){
            //ARRANGE
            List<Product> productList = createAndPersistProducts();
            productList = getSortedListByName(productList);

            String nameFilter = "x";
            Double minPriceFilter = 11.0;
            Double maxPriceFilter = 60.5;

            productList.removeIf(product -> !product.getName().toLowerCase().contains(nameFilter.toLowerCase()));
            productList.removeIf(product -> product.getPrice() <  minPriceFilter);
            productList.removeIf(product -> product.getPrice() >  maxPriceFilter);

            //ACT
            List<ProductResponseDTO> result = productRepository.getAllFiltered(nameFilter,minPriceFilter,maxPriceFilter);

            //ASSERT
            assertEquals(productList.size(), result.size());

            for (int i=0; i<result.size();i++){
                assertEquals(productList.get(i).getProductId(),result.get(i).productId());
                assertEquals(productList.get(i).getName(),result.get(i).name());
                assertEquals(productList.get(i).getPrice(),result.get(i).price());
            }
        }

    }
}