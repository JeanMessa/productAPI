package com.example.product.service;

import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductRequestDTO;
import com.example.product.domain.product.ProductResponseDTO;
import com.example.product.exception.ProductNotFoundException;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Nested
    class createProduct{

        @Test
        @DisplayName("Should map DTO to entity, save the entity to repository, and return the saved object")
        void createProduct_Success(){
            //ARRANGE
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone",10.5);
            UUID mockId = UUID.randomUUID();
            Product productMock = new Product();
            productMock.setProductId(mockId);
            productMock.setName("Smartphone");
            productMock.setPrice(10.5);

            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

            when(productRepository.save(any(Product.class))).thenReturn(productMock);

            //ACT
            Product createdProduct = productService.createProduct(productRequestDTO);

            //ASSERT
            verify(productRepository,times(1)).save(productCaptor.capture());

            Product capturedProduct = productCaptor.getValue();

            assertNull(capturedProduct.getProductId());
            assertEquals(capturedProduct.getName(),productRequestDTO.name());
            assertEquals(capturedProduct.getPrice(),productRequestDTO.price());

            assertEquals(createdProduct.getProductId(),productMock.getProductId());
            assertEquals(createdProduct.getName(),productMock.getName());
            assertEquals(createdProduct.getPrice(),productMock.getPrice());
        }
    }

    @Nested
    class getAllProducts{

        @Test
        @DisplayName("Should return a list of products when no filters are applied")
        void getAllProducts_WithoutFilters_ReturnList(){
            //ARRANGE
            ProductResponseDTO productResponseDTO1 = new ProductResponseDTO(UUID.randomUUID(),"Smartphone 1",11.0);
            ProductResponseDTO productResponseDTO2 = new ProductResponseDTO(UUID.randomUUID(),"Smartphone 2",13.0);

            List<ProductResponseDTO> productResponseDTOListMock = List.of(productResponseDTO1,productResponseDTO2);

            when(productRepository.getAllFiltered(null,null,null)).thenReturn(productResponseDTOListMock);

            //ACT
            List<ProductResponseDTO> productsResult = productService.getAllProducts(null,null,null);

            //ASSERT
            verify(productRepository,times(1)).getAllFiltered(null,null,null);

            assertNotNull(productsResult);
            assertEquals(2, productsResult.size());
            assertEquals(productResponseDTOListMock,productsResult);
        }

        @Test
        @DisplayName("Should return a list of products when all filters are applied")
        void getAllProducts_WithAllFilters_ReturnList(){
            //ARRANGE
            ProductResponseDTO productResponseDTO1 = new ProductResponseDTO(UUID.randomUUID(),"Smartphone 1",11.0);
            ProductResponseDTO productResponseDTO2 = new ProductResponseDTO(UUID.randomUUID(),"Smartphone 2",13.0);
            String name = "Smartphone";
            Double minPrice = 10.0;
            Double maxPrice = 15.0;

            List<ProductResponseDTO> productResponseDTOListMock = List.of(productResponseDTO1,productResponseDTO2);

            when(productRepository.getAllFiltered(name,minPrice,maxPrice)).thenReturn(productResponseDTOListMock);

            //ACT
            List<ProductResponseDTO> productsResult = productService.getAllProducts(name,minPrice,maxPrice);

            //ASSERT
            verify(productRepository,times(1)).getAllFiltered(name,minPrice,maxPrice);

            assertNotNull(productsResult);
            assertEquals(2, productsResult.size());
            assertEquals(productResponseDTOListMock,productsResult);
        }

        @Test
        @DisplayName("Should return an empty list when products do not match filters")
        void getAllProducts_WithFilters_ReturnEmptyList(){
            //ARRANGE
            String name = "Smartphone";
            Double minPrice = 10.0;
            Double maxPrice = 15.0;

            List<ProductResponseDTO> productResponseDTOListMock = List.of();

            when(productRepository.getAllFiltered(name,minPrice,maxPrice)).thenReturn(productResponseDTOListMock);

            //ACT
            List<ProductResponseDTO> productsResult = productService.getAllProducts(name,minPrice,maxPrice);

            //ASSERT
            verify(productRepository,times(1)).getAllFiltered(name,minPrice,maxPrice);

            assertNotNull(productsResult);
            assertEquals(0, productsResult.size());
            assertEquals(productResponseDTOListMock,productsResult);
        }

    }

    @Nested
    class getProduct{

        @Test
        @DisplayName("Should return the product when product's ID is found")
        void getProduct_Found(){
            //ARRANGE
            UUID productId = UUID.randomUUID();

            Product productMock = new Product();
            productMock.setProductId(productId);
            productMock.setName("Smartphone");
            productMock.setPrice(10.5);

            when(productRepository.findById(productId)).thenReturn(Optional.of(productMock));

            //ACT
            ProductResponseDTO productResponseDTOResult = productService.getProduct(productId);

            //ASSERT
            verify(productRepository,times(1)).findById(productId);

            assertNotNull(productResponseDTOResult);
            assertEquals(productMock.getProductId(),productResponseDTOResult.productId());
            assertEquals(productMock.getName(),productResponseDTOResult.name());
            assertEquals(productMock.getPrice(),productResponseDTOResult.price());
        }

        @Test
        @DisplayName("Should throw exception when product's ID is not found")
        void getProduct_NotFound(){
            //ARRANGE
            UUID nonExistentId = UUID.randomUUID();

            when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            //ACT & ASSERT
            ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,() -> {
                productService.getProduct(nonExistentId);
            });

            //ASSERT
            verify(productRepository,times(1)).findById(nonExistentId);

            assertEquals("Product not found.",exception.getMessage());
        }

    }

    @Nested
    class updateProduct{
        @Test
        @DisplayName("Should update all fields when product's ID is found and both parameters are not null")
        void updateProduct_UpdateAllFields_Success(){
            //ARRANGE
            UUID productId = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone X",10.7);

            Product productMock = new Product();
            productMock.setProductId(productId);
            productMock.setName("Smartphone");
            productMock.setPrice(10.5);

            Product productUpdatedMock = new Product();
            productUpdatedMock.setProductId(productId);
            productUpdatedMock.setName("Smartphone X");
            productUpdatedMock.setPrice(10.7);

            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

            when(productRepository.findById(productId)).thenReturn(Optional.of(productMock));
            when(productRepository.save(any(Product.class))).thenReturn(productUpdatedMock);

            //ACT
            Product productResult = productService.updateProduct(productId,productRequestDTO);

            //ASSERT
            verify(productRepository,times(1)).findById(productId);
            verify(productRepository,times(1)).save(productCaptor.capture());

            Product capturedProduct = productCaptor.getValue();

            assertNotNull(capturedProduct);
            assertEquals(productId,capturedProduct.getProductId());
            assertEquals(productRequestDTO.name(),capturedProduct.getName());
            assertEquals(productRequestDTO.price(),capturedProduct.getPrice());

            assertNotNull(productResult);
            assertEquals(productId,productResult.getProductId());
            assertEquals(productRequestDTO.name(),productResult.getName());
            assertEquals(productRequestDTO.price(),productResult.getPrice());
        }

        @Test
        @DisplayName("Should update name when product's ID is found and only price are null")
        void updateProduct_UpdateName_Success(){
            //ARRANGE
            UUID productId = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone X",null);

            Product productMock = new Product();
            productMock.setProductId(productId);
            productMock.setName("Smartphone");
            productMock.setPrice(10.5);

            Product productUpdatedMock = new Product();
            productUpdatedMock.setProductId(productId);
            productUpdatedMock.setName("Smartphone X");
            productUpdatedMock.setPrice(10.5);

            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

            when(productRepository.findById(productId)).thenReturn(Optional.of(productMock));
            when(productRepository.save(any(Product.class))).thenReturn(productUpdatedMock);

            //ACT
            Product productResult = productService.updateProduct(productId,productRequestDTO);

            //ASSERT
            verify(productRepository,times(1)).findById(productId);
            verify(productRepository,times(1)).save(productCaptor.capture());

            Product capturedProduct = productCaptor.getValue();

            assertNotNull(capturedProduct);
            assertEquals(productId,capturedProduct.getProductId());
            assertEquals(productRequestDTO.name(),capturedProduct.getName());
            assertEquals(productMock.getPrice(),capturedProduct.getPrice());

            assertNotNull(productResult);
            assertEquals(productId,productResult.getProductId());
            assertEquals(productRequestDTO.name(),productResult.getName());
            assertEquals(productMock.getPrice(),productResult.getPrice());
        }

        @Test
        @DisplayName("Should update price when product's ID is found and only name are null")
        void updateProduct_UpdatePrice_Success(){
            //ARRANGE
            UUID productId = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO(null,10.7);

            Product productMock = new Product();
            productMock.setProductId(productId);
            productMock.setName("Smartphone");
            productMock.setPrice(10.5);

            Product productUpdatedMock = new Product();
            productUpdatedMock.setProductId(productId);
            productUpdatedMock.setName("Smartphone");
            productUpdatedMock.setPrice(10.7);

            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

            when(productRepository.findById(productId)).thenReturn(Optional.of(productMock));
            when(productRepository.save(any(Product.class))).thenReturn(productUpdatedMock);

            //ACT
            Product productResult = productService.updateProduct(productId,productRequestDTO);

            //ASSERT
            verify(productRepository,times(1)).findById(productId);
            verify(productRepository,times(1)).save(productCaptor.capture());

            Product capturedProduct = productCaptor.getValue();

            assertNotNull(capturedProduct);
            assertEquals(productId,capturedProduct.getProductId());
            assertEquals(productMock.getName(),capturedProduct.getName());
            assertEquals(productRequestDTO.price(),capturedProduct.getPrice());

            assertNotNull(productResult);
            assertEquals(productId,productResult.getProductId());
            assertEquals(productMock.getName(),productResult.getName());
            assertEquals(productRequestDTO.price(),productResult.getPrice());
        }

        @Test
        @DisplayName("Should throw exception when product's ID is not found and shouldn't call save of productRepository")
        void updateProduct_NotFound_ThrowException(){
            //ARRANGE
            UUID nonExistentId = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone X",10.7);

            when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            //ACT & ASSERT
            ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,() -> {
                productService.updateProduct(nonExistentId,productRequestDTO);
            });

            //ASSERT
            verify(productRepository,times(1)).findById(nonExistentId);
            verify(productRepository,never()).save(any(Product.class));

            assertEquals("Product not found.",exception.getMessage());
        }
    }

    @Nested
    class deleteProduct{

        @Test
        @DisplayName("Should delete if product's id is found")
        void deleteProduct_Found(){
            //ARRANGE
            UUID productId = UUID.randomUUID();

            Product productMock = new Product();

            when(productRepository.findById(productId)).thenReturn(Optional.of(productMock));

            //ACT
            productService.deleteProduct(productId);

            //ASSERT
            verify(productRepository,times(1)).findById(productId);
            verify(productRepository,times(1)).deleteById(productId);
        }

        @Test
        @DisplayName("Should throw exception and shouldn't call deleteById of productRepository if product's id is not found ")
        void deleteProduct_NotFound(){
            //ARRANGE
            UUID nonExistentId = UUID.randomUUID();

            when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            //ACT & ASSERT
            ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,() -> {
                productService.deleteProduct(nonExistentId);
            });

            //ASSERT
            verify(productRepository,times(1)).findById(nonExistentId);
            verify(productRepository,never()).deleteById(nonExistentId);

            assertEquals("Product not found.",exception.getMessage());
        }

    }

}