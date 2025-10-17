package com.example.product.service;

import com.example.product.domain.user.*;
import com.example.product.exception.UsernameAlreadyInUseException;
import com.example.product.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    class create{

        @Test
        @DisplayName("Should encode password and create user when the username not exist yet.")
        void create_Success(){
            //ARRANGE
            String username = "userTest";
            String password = "123";
            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("userTest",password, UserRole.ADMIN);
            String encryptedPassword = "encrypted";

            when(userRepository.findByUsername(username)).thenReturn(null);
            when(passwordEncoder.encode(password)).thenReturn(encryptedPassword);

            ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

            //ACT
            userService.create(registerRequestDTO);

            //ASSERT
            verify(userRepository,times(1)).findByUsername(username);
            verify(passwordEncoder,times(1)).encode(password);
            verify(userRepository,times(1)).save(userArgumentCaptor.capture());

            User userCaptured = userArgumentCaptor.getValue();

            assertEquals(registerRequestDTO.username(),userCaptured.getUsername());
            assertEquals(encryptedPassword,userCaptured.getPassword());
            assertEquals(registerRequestDTO.role(),userCaptured.getRole());
        }

        @Test
        @DisplayName("Should throw exception when the username is already in use.")
        void create_UsernameAlreadyInUse_ThrowsException(){
            //ARRANGE
            String username = "userTest";
            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("userTest","123", UserRole.ADMIN);
            User userMock = new User(UUID.randomUUID(),username,"12345",UserRole.COMMON);

            when(userRepository.findByUsername(username)).thenReturn(userMock);

            //ACT & ASSERT
            UsernameAlreadyInUseException exception = assertThrows(UsernameAlreadyInUseException.class, () -> {
                userService.create(registerRequestDTO);
            });

            //ASSERT
            verify(userRepository,times(1)).findByUsername(username);
            verify(userRepository,never()).save(any(User.class));

            assertEquals("This username is already in use.", exception.getMessage());
        }
    }

    @Nested
    class login{
        @Test
        @DisplayName("Should return token,username and role when credentials are correctly.")
        void login_success(){
            //ARRANGE
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO("userTest","123");
            Authentication authenticationMock = mock(Authentication.class);
            User userMock = new User(UUID.randomUUID(),"userTest","encrypted",UserRole.ADMIN);
            String tokenMock = "Mocked Token";

            when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userMock);
            when(tokenService.generateToken(userMock)).thenReturn(tokenMock);

            //ACT
            LoginResponseDTO loginResponseDTOResult = userService.login(loginRequestDTO);

            //ASSERT
            verify(authenticationManager,times(1)).authenticate(any());
            verify(tokenService,times(1)).generateToken(userMock);

            assertEquals(tokenMock,loginResponseDTOResult.token());
            assertEquals(loginRequestDTO.username(),loginResponseDTOResult.username());
            assertEquals(userMock.getRole(),loginResponseDTOResult.role());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when password is not found.")
        void login_IncorrectUsername_ThrowBadCredentialsException(){
            //ARRANGE
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO("nonExistentUser","123");

            when(authenticationManager.authenticate(any())).thenThrow(InternalAuthenticationServiceException.class);

            //ACT & ASSERT
            BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
                userService.login(loginRequestDTO);
            });

            //ASSERT
            verify(authenticationManager,times(1)).authenticate(any());
            verify(tokenService,never()).generateToken(any(User.class));

            assertEquals("User or password incorrect.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when password is incorrect.")
        void login_IncorrectPassword_ThrowBadCredentialsException(){
            //ARRANGE
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO("userTest","incorrect Password");

            when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

            //ACT & ASSERT
            assertThrows(BadCredentialsException.class, () -> {
                userService.login(loginRequestDTO);
            });

            //ASSERT
            verify(authenticationManager,times(1)).authenticate(any());
            verify(tokenService,never()).generateToken(any(User.class));
        }
    }

}