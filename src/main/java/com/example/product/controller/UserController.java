package com.example.product.controller;

import com.example.product.domain.user.RegisterRequestDTO;
import com.example.product.domain.user.RegisterResponseDTO;
import com.example.product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        RegisterResponseDTO registerResponseDTO = userService.create(data);
        return new ResponseEntity<>(registerResponseDTO.message(),registerResponseDTO.httpStatus());
    }
}
