package com.example.product.repository;

import com.example.product.domain.user.User;
import com.example.product.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Nested
    class findByUsername{

        @DisplayName("Should return a correct user details when an existent username is passed.")
        @Test
        void findByUsername_ExistentUsername_ReturnsUserDetails(){
            //ARRANGE
            String username = "userTest";
            User expectedUser = testEntityManager.persistAndFlush(new User(null,username,"123", UserRole.ADMIN));

            //ACT
            UserDetails userDetailsResult = userRepository.findByUsername(username);

            //ASSERT
            assertEquals(expectedUser.getUsername(),userDetailsResult.getUsername());
            assertEquals(expectedUser.getPassword(),userDetailsResult.getPassword());
            assertEquals(expectedUser.getAuthorities(),userDetailsResult.getAuthorities());
        }

        @DisplayName("Should return null when an non existent username is passed.")
        @Test
        void findByUsername_NonExistentUsername_ReturnsNull(){
            //ARRANGE
            String nonExistentUsername = "nonExistentUser";

            //ACT
            UserDetails userDetailsResult = userRepository.findByUsername(nonExistentUsername);

            //ASSERT
            assertNull(userDetailsResult);
        }

    }

}