package com.example.product.service;

import com.example.product.domain.user.RegisterRequestDTO;
import com.example.product.domain.user.RegisterResponseDTO;
import com.example.product.domain.user.User;
import com.example.product.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public RegisterResponseDTO create(RegisterRequestDTO data){
        if (userRepository.findByUsername(data.username())!=null){
            return new RegisterResponseDTO(HttpStatus.BAD_REQUEST,"This username is already in use.");
        }else{
            User newUser = new User();

            newUser.setUsername(data.username());

            String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
            newUser.setPassword(encryptedPassword);

            newUser.setRole(data.role());

            userRepository.save(newUser);
            return new RegisterResponseDTO(HttpStatus.OK,"User registered successfully.");
        }
    }
}
