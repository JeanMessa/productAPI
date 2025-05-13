package com.example.product.domain.user;

import org.springframework.http.HttpStatus;

public record RegisterResponseDTO(HttpStatus httpStatus, String message) {
}
