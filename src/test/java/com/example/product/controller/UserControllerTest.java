package com.example.product.controller;

import com.example.product.domain.user.LoginRequestDTO;
import com.example.product.domain.user.LoginResponseDTO;
import com.example.product.domain.user.RegisterRequestDTO;
import com.example.product.domain.user.UserRole;
import com.example.product.exception.UsernameAlreadyInUseException;
import com.example.product.repository.UserRepository;
import com.example.product.service.TokenService;
import com.example.product.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    private final String PRODUCT_API_USER_URL = "/user";

    @Nested
    class register{

        @DisplayName("Should return 201 when body is valid and username not already exist.")
        @Test
        void register_ValidBodyAndNewUsername_Return201() throws Exception{
            //ARRANGE
            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("userTest","123", UserRole.ADMIN);

            doNothing().when(userService).create(registerRequestDTO);

            //ACT
            mockMvc.perform(post(PRODUCT_API_USER_URL+"/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequestDTO)))

                    //ASSERT

                    .andExpect(status().isCreated())

                    .andExpect(content().string("User registered successfully."))

                    .andDo(result -> verify(userService,times(1)).create(registerRequestDTO));
        }

        @DisplayName("Should return 400 and user already in use message when body is valid and username is already in use.")
        @Test
        void register_ValidBodyAndUsernameAlreadyInUse_Return400AndCorrectMessage() throws Exception{
            //ARRANGE
            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("userTest","123", UserRole.ADMIN);

            String expectedMessage = "This username is already in use.";

            doThrow(new UsernameAlreadyInUseException(expectedMessage)).when(userService).create(registerRequestDTO);

            //ACT
            mockMvc.perform(post(PRODUCT_API_USER_URL+"/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequestDTO)))

                    //ASSERT

                    .andExpect(status().isBadRequest())

                    .andExpect(content().string(expectedMessage))

                    .andDo(result -> verify(userService,times(1)).create(registerRequestDTO));
        }

        @DisplayName("Should return 400 and list of incorrect fields when receive invalid body.")
        @Test
        void register_InvalidBody_Return400AndListOfIncorrectFields() throws Exception{
            //ARRANGE
            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(null,null, null);

            //ACT
            mockMvc.perform(post(PRODUCT_API_USER_URL+"/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequestDTO)))

                    //ASSERT

                    .andExpect(status().isBadRequest())

                    .andExpect(jsonPath("$[?(@.field == 'username')].field").value("username"))
                    .andExpect(jsonPath("$[?(@.field == 'username')].message").value("The username is required."))

                    .andExpect(jsonPath("$[?(@.field == 'password')].field").value("password"))
                    .andExpect(jsonPath("$[?(@.field == 'password')].message").value("The password is required."))

                    .andExpect(jsonPath("$[?(@.field == 'role')].field").value("role"))
                    .andExpect(jsonPath("$[?(@.field == 'role')].message").value("The role is required."))

                    .andDo(result -> verify(userService,never()).create(registerRequestDTO));
        }
    }

    @Nested
    class login{

        @DisplayName("Should return 200, token, username and role when body is valid and credentials is correct.")
        @Test
        void login_ValidBodyAndCorrectCredentials_Return200TokenUsernameAndRole() throws Exception{
            //ARRANGE
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO("userTest","123");

            LoginResponseDTO loginResponseDTOMock= new LoginResponseDTO("Valid Token","userTest",UserRole.ADMIN);

            when(userService.login(loginRequestDTO)).thenReturn(loginResponseDTOMock);

            //ACT
            mockMvc.perform(post(PRODUCT_API_USER_URL+"/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestDTO)))

                    //ASSERT

                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.token").value(loginResponseDTOMock.token()))
                    .andExpect(jsonPath("$.username").value(loginRequestDTO.username()))
                    .andExpect(jsonPath("$.role").value(loginResponseDTOMock.role().toString()))

                    .andDo(result -> verify(userService,times(1)).login(loginRequestDTO));
        }

        @DisplayName("Should return 401 and correct message when body is valid and credentials incorrect.")
        @Test
        void login_ValidBodyAndIncorrectCredentials_Return401AndCorrectMessage() throws Exception{
            //ARRANGE
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO("userTest","123");

            String expectedMessage = "User or password incorrect.";

            when(userService.login(loginRequestDTO)).thenThrow(new BadCredentialsException(expectedMessage));

            //ACT
            mockMvc.perform(post(PRODUCT_API_USER_URL+"/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestDTO)))

                    //ASSERT

                    .andExpect(status().isUnauthorized())

                    .andExpect(content().string(expectedMessage))

                    .andDo(result -> verify(userService,times(1)).login(loginRequestDTO));
        }

        @DisplayName("Should return 400 and list of incorrect fields when receive invalid body.")
        @Test
        void login_InvalidBody_Return400AndListOfIncorrectFields() throws Exception{
            //ARRANGE
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO(null,null);

            //ACT
            mockMvc.perform(post(PRODUCT_API_USER_URL+"/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestDTO)))

                    //ASSERT

                    .andExpect(status().isBadRequest())

                    .andExpect(jsonPath("$[?(@.field == 'username')].field").value("username"))
                    .andExpect(jsonPath("$[?(@.field == 'username')].message").value("The username is required."))

                    .andExpect(jsonPath("$[?(@.field == 'password')].field").value("password"))
                    .andExpect(jsonPath("$[?(@.field == 'password')].message").value("The password is required."))

                    .andDo(result -> verify(userService,never()).login(loginRequestDTO));
        }

    }

}