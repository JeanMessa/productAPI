package com.example.product.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
        @NotBlank(message = "The username is required.")
        String username,
        @NotBlank(message = "The password is required.")
        String password,
        @NotNull(message = "The role is required.")
        UserRole role
) {}
