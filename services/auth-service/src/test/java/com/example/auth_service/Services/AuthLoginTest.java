package com.example.auth_service.Services;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.Models.AuthIdentity;
import com.example.auth_service.Models.RefreshToken;
import com.example.auth_service.Models.User;
import com.example.auth_service.Service.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthLoginTest extends AuthServiceImplementationTest{

    @Test
    void login_shouldSuccessfullyLoginUser() {

        // =========================
        // Arrange
        // =========================

        LoginRequest request = new LoginRequest();

        request.setEmail("test@example.com");
        request.setPassword("password");


        // Existing user
        User user = new User();

        UUID userId = UUID.randomUUID();

        user.setId(userId);
        user.setEmail(request.getEmail());
        user.setPhoneNumber("+91123456789");
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setStatus("ACTIVE");


        // UserRepository returns existing user
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(java.util.Optional.of(user));


        // Existing LOCAL authentication identity
        AuthIdentity identity = new AuthIdentity();

        identity.setUser(user);
        identity.setProvider("LOCAL");
        identity.setProviderUserId(request.getEmail());
        identity.setPasswordHash("hashedPassword");

        when(authIdentityRepository.findByUserIdAndProvider(
                userId,
                "LOCAL"
        )).thenReturn(java.util.Optional.of(identity));


        // Password is correct
        when(passwordEncoder.matches(
                request.getPassword(),
                identity.getPasswordHash()
        )).thenReturn(true);


        // JWT
        when(jwtService.generateAccessToken(userId))
                .thenReturn("access-token");


        // Refresh token
        when(refreshTokenService.generateRefreshToken())
                .thenReturn("refresh-token");

        when(refreshTokenService.hashToken("refresh-token"))
                .thenReturn("hashed-refresh-token");

//        when(jwtService.getAccessTokenExpiration())
//                .thenReturn(900L);


        // =========================
        // Act
        // =========================

        AuthResponse response = authService.login(request);


        // =========================
        // Assert
        // =========================

        assertNotNull(response);

        assertEquals(
                "access-token",
                response.getAccessToken()
        );

        assertEquals(
                "refresh-token",
                response.getRefreshToken()
        );

        assertEquals(
                "Bearer",
                response.getTokenType()
        );

        assertEquals(
                900L,
                response.getExpiresIn()
        );

        assertNotNull(response.getUser());

        assertEquals(
                userId,
                response.getUser().getId()
        );

        assertEquals(
                "test@example.com",
                response.getUser().getEmail()
        );


        // =========================
        // Verify
        // =========================

        verify(userRepository)
                .findByEmail(request.getEmail());

        verify(authIdentityRepository)
                .findByUserIdAndProvider(userId, "LOCAL");

        verify(passwordEncoder)
                .matches(
                        request.getPassword(),
                        identity.getPasswordHash()
                );

        verify(jwtService)
                .generateAccessToken(userId);

        verify(refreshTokenService)
                .generateRefreshToken();

        verify(refreshTokenService)
                .hashToken("refresh-token");

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
    }

    @Test
    void login_shouldThrowExceptionWhenPasswordIsWrong() {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail(request.getEmail());
        user.setStatus("ACTIVE");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        AuthIdentity identity = new AuthIdentity();
        identity.setUser(user);
        identity.setProvider("LOCAL");
        identity.setProviderUserId(request.getEmail());
        identity.setPasswordHash("correctHashedPassword");

        when(authIdentityRepository.findByUserIdAndProvider(
                userId,
                "LOCAL"
        )).thenReturn(Optional.of(identity));

        when(passwordEncoder.matches(
                request.getPassword(),
                identity.getPasswordHash()
        )).thenReturn(false);


        // Act + Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid password",
                exception.getMessage()
        );


        // Verify
        verify(userRepository)
                .findByEmail(request.getEmail());

        verify(authIdentityRepository)
                .findByUserIdAndProvider(userId, "LOCAL");

        verify(passwordEncoder)
                .matches(
                        request.getPassword(),
                        identity.getPasswordHash()
                );

        // No tokens should be generated
        verify(jwtService, never())
                .generateAccessToken(any(UUID.class));

        verify(refreshTokenService, never())
                .generateRefreshToken();

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void login_shouldThrowExceptionWhenUserDoesNotExist() {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("unknown@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());


        // Act + Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(request)
                );


        // Assert
        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );


        // Verify
        verify(userRepository)
                .findByEmail(request.getEmail());

        // These must NOT happen
        verify(authIdentityRepository, never())
                .findByUserIdAndProvider(any(UUID.class), anyString());

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateAccessToken(any(UUID.class));

        verify(refreshTokenService, never())
                .generateRefreshToken();

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void login_shouldThrowExceptionWhenLocalIdentityDoesNotExist() {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("test@example.com");
        request.setPassword("password");

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail(request.getEmail());
        user.setStatus("ACTIVE");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(authIdentityRepository.findByUserIdAndProvider(
                userId,
                "LOCAL"
        )).thenReturn(Optional.empty());


        // Act + Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );


        // Verify
        verify(userRepository)
                .findByEmail(request.getEmail());

        verify(authIdentityRepository)
                .findByUserIdAndProvider(userId, "LOCAL");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateAccessToken(any(UUID.class));

        verify(refreshTokenService, never())
                .generateRefreshToken();

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }
}
