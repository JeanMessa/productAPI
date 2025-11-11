package com.example.product.exception;

import com.example.product.domain.error.ValidationErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> RuntimeException(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again.");
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> ProductNotFoundException(ProductNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyInUseException.class)
    public ResponseEntity<String> UsernameAlreadyInUseException(UsernameAlreadyInUseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> BadCredentialsExceptionException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User or password incorrect.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDTO>> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<ValidationErrorDTO> errors = ex.getFieldErrors().stream()
                .map(ValidationErrorDTO::new)
                .toList();

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> MethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if (ex.getName().equals("productId")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid format for Product Id, the format must be a valid UUID.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid format for " + ex.getName() + " Parameter.");
        }
    }
}
