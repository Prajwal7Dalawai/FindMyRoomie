package com.example.auth_service.Services;

import com.example.auth_service.Models.RefreshToken;
import com.example.auth_service.Models.User;
import com.example.auth_service.Service.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogoutTest extends AuthServiceImplementationTest{
    @Test
    void logout_shouldRevokeRefreshTokenWhenTokenIsValid() {

        String rawRefreshToken = "valid-refresh-token";
        String hashedRefreshToken = "hashed-refresh-token";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

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

        // Act
        authService.logout(rawRefreshToken);

        // Assert
        assertTrue(refreshToken.isRevoked());

        verify(refreshTokenService)
                .hashToken(rawRefreshToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hashedRefreshToken);

        verify(refreshTokenRepository)
                .save(refreshToken);
    }

    @Test
    void logout_shouldThrowExceptionWhenTokenDoesNotExist() {

        String rawRefreshToken = "invalid-refresh-token";
        String hashedRefreshToken = "hashed-invalid-refresh-token";

        when(refreshTokenService.hashToken(rawRefreshToken))
                .thenReturn(hashedRefreshToken);

        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken))
                .thenReturn(Optional.empty());

        // Act + Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.logout(rawRefreshToken)
                );

        assertEquals(
                "Invalid refresh token",
                exception.getMessage()
        );

        verify(refreshTokenService)
                .hashToken(rawRefreshToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hashedRefreshToken);

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }
}
