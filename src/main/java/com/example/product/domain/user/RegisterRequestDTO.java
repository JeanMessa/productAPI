package com.example.product.domain.user;

public record RegisterRequestDTO(String username, String password,UserRole role) {
}
