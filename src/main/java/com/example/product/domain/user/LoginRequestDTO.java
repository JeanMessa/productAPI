package com.example.product.domain.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "The username is required.")
        String username,
        @NotBlank(message = "The password is required.")
        String password
) {}
