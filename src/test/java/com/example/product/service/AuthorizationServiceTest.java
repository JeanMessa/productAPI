package com.example.product.service;

import com.example.product.domain.user.User;
import com.example.product.domain.user.UserRole;
import com.example.product.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AuthorizationService authorizationService;

    @Nested
    class loadUserByUsername{

        @DisplayName("Should return UserDetails when username is found.")
        @Test
        void loadUserByUsername_Success(){
            //ARRANGE
            String username = "userTest";

            UserDetails userMock = new User(UUID.randomUUID(),"userTest","123", UserRole.ADMIN);

            when(userRepository.findByUsername(username)).thenReturn(userMock);

            //ACT
            UserDetails result = authorizationService.loadUserByUsername(username);

            //ASSERT
            verify(userRepository,times(1)).findByUsername(username);

            assertEquals(userMock.getUsername(),result.getUsername());
            assertEquals(userMock.getPassword(),result.getPassword());
            assertEquals(userMock.getAuthorities(),result.getAuthorities());
        }

        @DisplayName("Should return null when username is not found.")
        @Test
        void loadUserByUsername_ReturnsNull(){
            //ARRANGE
            String username = "nonExistentUser";

            when(userRepository.findByUsername(username)).thenReturn(null);

            //ACT
            UserDetails result = authorizationService.loadUserByUsername(username);

            //ASSERT
            verify(userRepository,times(1)).findByUsername(username);

            assertNull(result);
        }

    }

}