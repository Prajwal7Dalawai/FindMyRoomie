package com.example.auth_service.Services;

import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.Models.RefreshToken;
import com.example.auth_service.Models.User;
import com.example.auth_service.Service.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RefreshTokenTest extends AuthServiceImplementationTest {
    @Test
    void refreshToken_shouldReturnNewAccessTokenWhenTokenIsValid() {
        String rawRefreshToken = "refresh-token";
        String hashedRefreshToken = "hashed-refresh-token";

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPhoneNumber("+91123456789");
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setStatus("ACTIVE");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashedRefreshToken);
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(
                OffsetDateTime.now().minusDays(1)
        );
        refreshToken.setExpiresAt(
                OffsetDateTime.now().plusDays(29)
        );

        when(refreshTokenService.hashToken(rawRefreshToken))
                .thenReturn(hashedRefreshToken);

        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken))
                .thenReturn(Optional.of(refreshToken));

        when(jwtService.generateAccessToken(userId))
                .thenReturn("new-access-token");

        AuthResponse response =
                authService.refreshToken(rawRefreshToken);

        // Result assertions
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals(rawRefreshToken, response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());

        assertNotNull(response.getUser());
        assertEquals(userId, response.getUser().getId());
        assertEquals("test@example.com", response.getUser().getEmail());

        // Interaction assertions
        verify(refreshTokenService)
                .hashToken(rawRefreshToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hashedRefreshToken);

        verify(jwtService)
                .generateAccessToken(userId);

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void refreshToken_shouldThrowExceptionWhenTokenDoesNotExist() {

        String rawRefreshToken = "invalid-token";
        String hashedRefreshToken = "hashed-invalid-token";

        when(refreshTokenService.hashToken(rawRefreshToken))
                .thenReturn(hashedRefreshToken);

        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken))
                .thenReturn(Optional.empty());

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.refreshToken(rawRefreshToken)
                );

        assertEquals("Invalid refresh token", exception.getMessage());

        verify(refreshTokenService)
                .hashToken(rawRefreshToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hashedRefreshToken);

        verify(jwtService, never())
                .generateAccessToken(any());

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void refreshToken_shouldThrowExceptionWhenTokenIsRevoked() {

        String rawRefreshToken = "revoked-token";
        String hashedRefreshToken = "hashed-revoked-token";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashedRefreshToken);
        refreshToken.setRevoked(true);
        refreshToken.setCreatedAt(
                OffsetDateTime.now().minusDays(10)
        );
        refreshToken.setExpiresAt(
                OffsetDateTime.now().plusDays(20)
        );

        when(refreshTokenService.hashToken(rawRefreshToken))
                .thenReturn(hashedRefreshToken);

        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken))
                .thenReturn(Optional.of(refreshToken));

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.refreshToken(rawRefreshToken)
                );

        assertEquals(
                "refresh Token has been revoked",
                exception.getMessage()
        );

        verify(refreshTokenService)
                .hashToken(rawRefreshToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hashedRefreshToken);

        verify(jwtService, never())
                .generateAccessToken(any());

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void refreshToken_shouldThrowExceptionWhenTokenIsExpired() {

        String rawRefreshToken = "expired-token";
        String hashedRefreshToken = "hashed-expired-token";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashedRefreshToken);
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(
                OffsetDateTime.now().minusDays(31)
        );
        refreshToken.setExpiresAt(
                OffsetDateTime.now().minusDays(1)
        );

        when(refreshTokenService.hashToken(rawRefreshToken))
                .thenReturn(hashedRefreshToken);

        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken))
                .thenReturn(Optional.of(refreshToken));

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.refreshToken(rawRefreshToken)
                );

        assertEquals(
                "refresh Token has expired",
                exception.getMessage()
        );

        verify(refreshTokenService)
                .hashToken(rawRefreshToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hashedRefreshToken);

        verify(jwtService, never())
                .generateAccessToken(any());

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }
}
