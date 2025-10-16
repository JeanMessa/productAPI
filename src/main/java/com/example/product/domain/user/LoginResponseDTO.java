package com.example.product.domain.user;

public record LoginResponseDTO(String token,String username,UserRole role) {
}
