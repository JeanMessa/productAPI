package com.example.product.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.product.domain.user.User;
import com.example.product.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"productApi.security.token.secret=test-key"})
@ContextConfiguration(classes = {TokenService.class})
class TokenServiceTest {


    @Autowired
    TokenService tokenService;

    private final String TEST_SECRET = "test-key";

    @Nested
    class generateToken{

        @Test
        @DisplayName("Should generate a token containing the correct subject")
        void generateToken_success(){


            //ARRANGE
            User user = new User(UUID.randomUUID(),"UserTest","123", UserRole.ADMIN);

            //ACT
            String token = tokenService.generateToken(user);

            //ASSERT
            assertNotNull(token);
            assertFalse(token.isEmpty());

            try {
                Algorithm algorithm = Algorithm.HMAC256(TEST_SECRET);
                String subject = JWT.require(algorithm)
                        .withIssuer("ProductApi")
                        .build()
                        .verify(token)
                        .getSubject();

                assertEquals(user.getUsername(),subject);
            }catch (Exception exception){
                fail("Fail in Token verification:  " + exception.getMessage());
            }
        }
    }

    @Nested
    class validateToken{

        @Test
        @DisplayName("Should return subject when token is valid.")
        void validateToken_success(){
            //ARRANGE
            String username = "UserTest";

            Algorithm algorithm = Algorithm.HMAC256(TEST_SECRET);
            String token = JWT.create()
                    .withIssuer("ProductApi")
                    .withSubject(username)
                    .sign(algorithm);


            //ACT
            String validateToken = tokenService.validateToken(token);

            //ASSERT
            assertNotNull(validateToken);
            assertEquals(username,validateToken);
        }

        @Test
        @DisplayName("Should return null when token is invalid.")
        void validateToken_fail(){
            //ARRANGE
            Algorithm algorithm = Algorithm.HMAC256("wrongSecret");
            String token = JWT.create()
                    .withIssuer("ProductApi")
                    .withSubject("UserTest")
                    .sign(algorithm);


            //ACT
            String validateToken = tokenService.validateToken(token);

            //ASSERT
            assertNull(validateToken);
        }
    }
}