package com.example.product.domain.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequestDTO(
        @NotBlank(message = "The name is required.")
        String name,

        @NotNull(message = "The price is required.")
        @Positive(message = "The price must be positive.")
        Double price) {
}
