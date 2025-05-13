package com.example.product.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("admin"),
    COMMON("common");

    private String role;

    UserRole(String role){this.role = role;}
}
