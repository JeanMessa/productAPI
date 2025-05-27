package com.example.product.domain.product;

import org.springframework.http.HttpStatus;

public record DeleteResponseDTO(HttpStatus httpStatus, String message) {
}
