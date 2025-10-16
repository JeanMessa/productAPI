package com.example.product.exception;

public class UsernameAlreadyInUseException extends RuntimeException{
    public UsernameAlreadyInUseException(String message) {super(message);}
}
