package com.example.product.controller;

import com.example.product.domain.user.LoginRequestDTO;
import com.example.product.domain.user.LoginResponseDTO;
import com.example.product.domain.user.RegisterRequestDTO;
import com.example.product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody RegisterRequestDTO data){
        userService.create(data);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data){
        return ResponseEntity.ok(userService.login(data));
    }
}
