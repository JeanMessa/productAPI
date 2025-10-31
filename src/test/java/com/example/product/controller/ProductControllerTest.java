package com.example.product.controller;

import com.example.product.domain.product.Product;
import com.example.product.domain.product.ProductRequestDTO;
import com.example.product.domain.product.ProductResponseDTO;
import com.example.product.exception.ProductNotFoundException;
import com.example.product.repository.UserRepository;
import com.example.product.service.ProductService;
import com.example.product.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    private final String PRODUCT_API_URL = "/product";

    @Nested
    class create{

        @Test
        @DisplayName("Should return 201 and the product created when receive correct body.")
        void create_WhenCorrectBody_Return201() throws Exception {
            //ARRANGE
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone",10.5);
            Product productMock = new Product(UUID.randomUUID(),"Smartphone",10.5);

            when(productService.createProduct(productRequestDTO)).thenReturn(productMock);

            //ACT
            mockMvc.perform(post(PRODUCT_API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))

                    //ASSERT
                    .andExpect(status().isCreated())

                    .andExpect(header().exists("Location"))
                    .andExpect(header().string("Location", String.format("http://localhost/product/%s",productMock.getProductId())))

                    .andExpect(jsonPath("$.productId").value(productMock.getProductId().toString()))
                    .andExpect(jsonPath("$.name").value(productRequestDTO.name()))
                    .andExpect(jsonPath("$.price").value(productRequestDTO.price().toString()))

                    .andDo(result -> verify(productService,times(1)).createProduct(productRequestDTO));
        }

        @Test
        @DisplayName("Should return 400 and list of incorrect fields when receive invalid body.")
        void create_WhenInvalidBody_Return400() throws Exception {
            //ARRANGE
            ProductRequestDTO productRequestDTO = new ProductRequestDTO(null,null);

            //ACT
            mockMvc.perform(post(PRODUCT_API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))

                    //ASSERT
                    .andExpect(status().isBadRequest())

                    .andExpect(jsonPath("$[?(@.field == 'name')].field").value("name"))
                    .andExpect(jsonPath("$[?(@.field == 'name')].message").value("The name is required."))

                    .andExpect(jsonPath("$[?(@.field == 'price')].field").value("price"))
                    .andExpect(jsonPath("$[?(@.field == 'price')].message").value("The price is required."))

                    .andExpect(jsonPath("$.length()").value(2))


                    .andDo(result -> verify(productService,never()).createProduct(any(ProductRequestDTO.class)));
        }
    }

    @Nested
    class getAll{

        @Test
        @DisplayName("Should return 200 and a list of found products when not receive filter parameters")
        void getAll_WithoutFilters_Return200AndProductList() throws Exception {
            //ARRANGE
            ProductResponseDTO productResponseDTO0 = new ProductResponseDTO(UUID.randomUUID(), "Smartphone 0", 11.0);
            ProductResponseDTO productResponseDTO1 = new ProductResponseDTO(UUID.randomUUID(), "Smartphone 1", 13.0);

            List<ProductResponseDTO> productResponseDTOListMock = List.of(productResponseDTO0, productResponseDTO1);

            when(productService.getAllProducts(null, null, null)).thenReturn(productResponseDTOListMock);

            //ACT
            mockMvc.perform(get(PRODUCT_API_URL))

                    //ASSERT
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$[0].productId").value(productResponseDTO0.productId().toString()))
                    .andExpect(jsonPath("$[0].name").value(productResponseDTO0.name()))
                    .andExpect(jsonPath("$[0].price").value(productResponseDTO0.price().toString()))

                    .andExpect(jsonPath("$[1].productId").value(productResponseDTO1.productId().toString()))
                    .andExpect(jsonPath("$[1].name").value(productResponseDTO1.name()))
                    .andExpect(jsonPath("$[1].price").value(productResponseDTO1.price().toString()))

                    .andExpect(jsonPath("$.length()").value(2))

                    .andDo(result -> verify(productService, times(1)).getAllProducts(isNull(), isNull(), isNull()));
        }

        @Test
        @DisplayName("Should return 200 and a list of found products when receive all filter parameters")
        void getAll_WithAllFilters_Return200AndProductList() throws Exception {
            //ARRANGE
            ProductResponseDTO productResponseDTO0 = new ProductResponseDTO(UUID.randomUUID(), "Smartphone 0", 11.0);
            ProductResponseDTO productResponseDTO1 = new ProductResponseDTO(UUID.randomUUID(), "Smartphone 1", 13.0);
            String name = "Smartphone";
            Double minPrice = 10.0;
            Double maxPrice = 15.0;

            List<ProductResponseDTO> productResponseDTOListMock = List.of(productResponseDTO0, productResponseDTO1);

            when(productService.getAllProducts(name, minPrice, maxPrice)).thenReturn(productResponseDTOListMock);

            //ACT
            mockMvc.perform(get(PRODUCT_API_URL)
                            .param("name",name)
                            .param("minPrice",minPrice.toString())
                            .param("maxPrice",maxPrice.toString())
                            .contentType(MediaType.APPLICATION_JSON))

                    //ASSERT
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$[0].productId").value(productResponseDTO0.productId().toString()))
                    .andExpect(jsonPath("$[0].name").value(productResponseDTO0.name()))
                    .andExpect(jsonPath("$[0].price").value(productResponseDTO0.price().toString()))

                    .andExpect(jsonPath("$[1].productId").value(productResponseDTO1.productId().toString()))
                    .andExpect(jsonPath("$[1].name").value(productResponseDTO1.name()))
                    .andExpect(jsonPath("$[1].price").value(productResponseDTO1.price().toString()))

                    .andExpect(jsonPath("$.length()").value(2))

                    .andDo(result -> verify(productService, times(1)).getAllProducts(name, minPrice, maxPrice));
        }

        @Test
        @DisplayName("Should return 200 and a empty list when receive all filter parameters")
        void getAll_WithAllFilters_Return200AndEmptyList() throws Exception {
            //ARRANGE
            String name = "Smartphone";
            Double minPrice = 10.0;
            Double maxPrice = 15.0;

            when(productService.getAllProducts(name, minPrice, maxPrice)).thenReturn(List.of());

            //ACT
            mockMvc.perform(get(PRODUCT_API_URL)
                            .param("name",name)
                            .param("minPrice",minPrice.toString())
                            .param("maxPrice",maxPrice.toString())
                            .contentType(MediaType.APPLICATION_JSON))

                    //ASSERT
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.length()").value(0))

                    .andDo(result -> verify(productService, times(1)).getAllProducts(name, minPrice, maxPrice));
        }
    }

    @Nested
    class get{

        @Test
        @DisplayName("Should return 200 and found product when UUID parameter is found")
        void get_ValidUUID_Return200AndProduct() throws Exception {
            //ARRANGE
            UUID productID = UUID.randomUUID();
            ProductResponseDTO productResponseDTO = new ProductResponseDTO(productID, "Smartphone", 10.5);

            when(productService.getProduct(productID)).thenReturn(productResponseDTO);

            //ACT
            mockMvc.perform(get(PRODUCT_API_URL + "/{id}", productID))

                    //ASSERT
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.productId").value(productResponseDTO.productId().toString()))
                    .andExpect(jsonPath("$.name").value(productResponseDTO.name()))
                    .andExpect(jsonPath("$.price").value(productResponseDTO.price().toString()))

                    .andDo(result -> verify(productService, times(1)).getProduct(productID));
        }

        @Test
        @DisplayName("Should return 404 when UUID is valid and non existent")
        void get_ValidNonExistentUUID_Return404() throws Exception {
            //ARRANGE
            UUID nonExistentId = UUID.randomUUID();

            String expectedMessage = "Product not found.";

            when(productService.getProduct(nonExistentId)).thenThrow(new ProductNotFoundException(expectedMessage));

            //ACT
            mockMvc.perform(get(PRODUCT_API_URL + "/{id}", nonExistentId))

                    //ASSERT
                    .andExpect(status().isNotFound())

                    .andExpect(content().string(expectedMessage))

                    .andDo(result -> verify(productService, times(1)).getProduct(nonExistentId));
        }

        @Test
        @DisplayName("Should return 400 when UUID is invalid.")
        void get_InvalidUUID_Return400() throws Exception {
            //ARRANGE
            String invalidFormatId = "Id in Invalid Format";

            //ACT
            mockMvc.perform(get(PRODUCT_API_URL + "/{id}", invalidFormatId))

                    //ASSERT
                    .andExpect(status().isBadRequest())

                    .andExpect(content().string("Invalid format for Product Id, the format must be a valid UUID."))

                    .andDo(result -> verify(productService, never()).getProduct(any(UUID.class)));
        }

    }

    @Nested
    class update{

        @Test
        @DisplayName("Should return 200 and product with name and price updated when all parameter is passed and UUID is found")
        void update_AllParametersAndValidUUID_Return200AndProductUpdated() throws Exception {
            //ARRANGE
            UUID productID = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone X",10.7);
            Product productMock = new Product(productID, "Smartphone X", 10.7);

            when(productService.updateProduct(productID,productRequestDTO)).thenReturn(productMock);

            //ACT
            mockMvc.perform(put(PRODUCT_API_URL + "/{id}", productID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))

                    //ASSERT
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.productId").value(productID.toString()))
                    .andExpect(jsonPath("$.name").value(productRequestDTO.name()))
                    .andExpect(jsonPath("$.price").value(productRequestDTO.price().toString()))

                    .andDo(result -> verify(productService, times(1)).updateProduct(productID,productRequestDTO));
        }


        @Test
        @DisplayName("Should return 404 when UUID is valid and non existent")
        void update_ValidNonExistentUUID_Return404() throws Exception {
            //ARRANGE
            UUID nonExistentId = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone X",10.7);

            String expectedMessage = "Product not found.";

            when(productService.updateProduct(nonExistentId,productRequestDTO)).thenThrow(new ProductNotFoundException(expectedMessage));

            //ACT
            mockMvc.perform(put(PRODUCT_API_URL + "/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))

                    //ASSERT
                    .andExpect(status().isNotFound())

                    .andExpect(content().string(expectedMessage))

                    .andDo(result -> verify(productService, times(1)).updateProduct(nonExistentId,productRequestDTO));
        }

        @Test
        @DisplayName("Should return 400 when UUID is invalid.")
        void update_InvalidUUID_Return400() throws Exception {
            //ARRANGE
            String invalidFormatId = "Id in Invalid Format";
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone X",10.7);


            //ACT
            mockMvc.perform(put(PRODUCT_API_URL + "/{id}", invalidFormatId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))

                    //ASSERT
                    .andExpect(status().isBadRequest())

                    .andExpect(content().string("Invalid format for Product Id, the format must be a valid UUID."))

                    .andDo(result -> verify(productService, never()).updateProduct(any(UUID.class),any(ProductRequestDTO.class)));
        }

        @Test
        @DisplayName("Should return 200 and product with name updated when only price is null")
        void update_OnlyName_Return200AndProductUpdated() throws Exception {
            //ARRANGE
            UUID productID = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Smartphone X",null);
            Product productMock = new Product(productID, "Smartphone X", 10.5);

            when(productService.updateProduct(productID,productRequestDTO)).thenReturn(productMock);

            //ACT
            mockMvc.perform(put(PRODUCT_API_URL + "/{id}", productID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))

                    //ASSERT
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.productId").value(productID.toString()))
                    .andExpect(jsonPath("$.name").value(productRequestDTO.name()))
                    .andExpect(jsonPath("$.price").value(productMock.getPrice().toString()))

                    .andDo(result -> verify(productService, times(1)).updateProduct(productID,productRequestDTO));
        }

        @Test
        @DisplayName("Should return 200 and product with price updated when only name is null")
        void update_OnlyPrice_Return200AndProductUpdated() throws Exception {
            //ARRANGE
            UUID productID = UUID.randomUUID();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO(null,10.7);
            Product productMock = new Product(productID, "Smartphone", 10.7);

            when(productService.updateProduct(productID,productRequestDTO)).thenReturn(productMock);

            //ACT
            mockMvc.perform(put(PRODUCT_API_URL + "/{id}", productID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))

                    //ASSERT
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.productId").value(productID.toString()))
                    .andExpect(jsonPath("$.name").value(productMock.getName()))
                    .andExpect(jsonPath("$.price").value(productRequestDTO.price().toString()))

                    .andDo(result -> verify(productService, times(1)).updateProduct(productID,productRequestDTO));
        }

    }

    @Nested
    class delete{

        @Test
        @DisplayName("Should return 204 when UUID parameter is found")
        void delete_ValidUUID_Return204() throws Exception {
            //ARRANGE
            UUID productID = UUID.randomUUID();

            doNothing().when(productService).deleteProduct(productID);

            //ACT
            mockMvc.perform(delete(PRODUCT_API_URL + "/{id}", productID))

                    //ASSERT
                    .andExpect(status().isNoContent())

                    .andDo(result -> verify(productService, times(1)).deleteProduct(productID));
        }

        @Test
        @DisplayName("Should return 404 when UUID is valid and non existent")
        void delete_ValidNonExistentUUID_Return404() throws Exception {
            //ARRANGE
            UUID nonExistentId = UUID.randomUUID();

            String expectedMessage = "Product not found.";

            doThrow(new ProductNotFoundException(expectedMessage)).when(productService).deleteProduct(nonExistentId);

            //ACT
            mockMvc.perform(delete(PRODUCT_API_URL + "/{id}", nonExistentId))

                    //ASSERT
                    .andExpect(status().isNotFound())

                    .andExpect(content().string(expectedMessage))

                    .andDo(result -> verify(productService, times(1)).deleteProduct(nonExistentId));
        }

        @Test
        @DisplayName("Should return 400 when UUID is invalid.")
        void delete_InvalidUUID_Return400() throws Exception {
            //ARRANGE
            String invalidFormatId = "Id in Invalid Format";

            //ACT
            mockMvc.perform(delete(PRODUCT_API_URL + "/{id}", invalidFormatId))

                    //ASSERT
                    .andExpect(status().isBadRequest())

                    .andExpect(content().string("Invalid format for Product Id, the format must be a valid UUID."))

                    .andDo(result -> verify(productService, never()).deleteProduct(any(UUID.class)));
        }
    }

}
