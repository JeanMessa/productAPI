package com.example.product.service;

import com.example.product.domain.user.*;
import com.example.product.exception.UsernameAlreadyInUseException;
import com.example.product.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void create(RegisterRequestDTO data){
        if (userRepository.findByUsername(data.username())==null){
            User newUser = new User();

            newUser.setUsername(data.username());

            String encryptedPassword = passwordEncoder.encode(data.password());
            newUser.setPassword(encryptedPassword);

            newUser.setRole(data.role());

            userRepository.save(newUser);
        }else{
            throw new UsernameAlreadyInUseException("This username is already in use.");
        }
    }

    public LoginResponseDTO login(LoginRequestDTO data){
        User user = (User) userRepository.findByUsername(data.username());

        if (user!=null){
            try {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                String token = tokenService.generateToken((User) authentication.getPrincipal());

                return new LoginResponseDTO(token, data.username(), user.getRole(),true);
            }catch (BadCredentialsException e){
                return new LoginResponseDTO(null,null,null,false);
            }
        }else{
            return new LoginResponseDTO(null,null,null,false);
        }
    }
}
