package com.example.auth_service.Services;

import com.example.auth_service.DTO.request.RegisterRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.Models.AuthIdentity;
import com.example.auth_service.Models.User;
import com.example.auth_service.Service.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthRegistrationTest extends AuthServiceImplementationTest{

    @Test
    void register_shouldCreateUserAndAuthIdentity() {

        // =========================
        // Arrange
        // =========================

        RegisterRequest request = new RegisterRequest();

        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setPhoneNumber("+91123456789");

        // Email does not already exist
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        // Phone number does not already exist
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber()))
                .thenReturn(false);

        // Password encoding
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("hashedPassword");

        // Simulate saved User
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus("ACTIVE");
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        // Access token
        when(jwtService.generateAccessToken(any(UUID.class)))
                .thenReturn("access-token");

        // Refresh token
        when(refreshTokenService.generateRefreshToken())
                .thenReturn("refresh-token");

        // Refresh token hash
        when(refreshTokenService.hashToken("refresh-token"))
                .thenReturn("hashed-refresh-token");

        // Access token expiration
        when(jwtService.getAccessTokenExpiration())
                .thenReturn(900000L);


        // =========================
        // Act
        // =========================

        AuthResponse response = authService.register(request);


        // =========================
        // Assert
        // =========================

        assertNotNull(response);

        assertEquals("access-token", response.getAccessToken());

        assertEquals("refresh-token", response.getRefreshToken());

        assertEquals("Bearer", response.getTokenType());

        assertEquals(900000L, response.getExpiresIn());

        assertNotNull(response.getUser());

        assertEquals(user.getId(), response.getUser().getId());

        assertEquals(
                request.getEmail(),
                response.getUser().getEmail()
        );


        // =========================
        // Verify interactions
        // =========================

        verify(userRepository)
                .existsByEmail(request.getEmail());

        verify(userRepository)
                .existsByPhoneNumber(request.getPhoneNumber());

        verify(passwordEncoder)
                .encode(request.getPassword());

        verify(userRepository)
                .save(any(User.class));

        verify(authIdentityRepository)
                .save(any(AuthIdentity.class));

        verify(jwtService)
                .generateAccessToken(user.getId());

        verify(refreshTokenService)
                .generateRefreshToken();

        verify(refreshTokenService)
                .hashToken("refresh-token");

        verify(refreshTokenRepository)
                .save(any());

    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setEmail("existing@example.com");
        request.setPassword("password");
        request.setPhoneNumber("+91123456789");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);


        // Act + Assert
        ResourceAlreadyExistsException exception =
                assertThrows(
                        ResourceAlreadyExistsException.class,
                        () -> authService.register(request)
                );


        // Assert exception message
        assertEquals(
                "Email already exists",
                exception.getMessage()
        );


        // Verify
        verify(userRepository)
                .existsByEmail(request.getEmail());

        verify(userRepository, never())
                .existsByPhoneNumber(anyString());

        verify(userRepository, never())
                .save(any(User.class));

        verify(authIdentityRepository, never())
                .save(any(AuthIdentity.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void register_shouldThrowExceptionWhenPhoneNumberAlreadyExists() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setPhoneNumber("+91123456789");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(request.getPhoneNumber()))
                .thenReturn(true);


        // Act + Assert
        ResourceAlreadyExistsException exception =
                assertThrows(
                        ResourceAlreadyExistsException.class,
                        () -> authService.register(request)
                );


        // Assert exception message
        assertEquals(
                "Phone number already exists",
                exception.getMessage()
        );


        // Verify
        verify(userRepository)
                .existsByEmail(request.getEmail());

        verify(userRepository)
                .existsByPhoneNumber(request.getPhoneNumber());

        verify(userRepository, never())
                .save(any(User.class));

        verify(authIdentityRepository, never())
                .save(any(AuthIdentity.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }
}
