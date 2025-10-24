package com.example.product.domain.error;

public record ValidationErrorDTO(String field, String message) {
    public ValidationErrorDTO(org.springframework.validation.FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}