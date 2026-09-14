package com.example.auth_service.Security;

import com.example.auth_service.Service.impl.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret =
            "this-is-a-very-long-secret-key-for-testing-jwt-security";

    private final long expiration = 900000; // 15 minutes

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expiration);
    }

    @Test
    void generateAccessToken_shouldReturnValidToken() {

        UUID userId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId);

        assertNotNull(token);
        assertFalse(token.isBlank());

        // JWT consists of:
        // header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateAccessToken_shouldContainCorrectUserId() {

        UUID userId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId);

        UUID extractedUserId =
                jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void extractUserId_shouldThrowExceptionWhenTokenIsExpired() {

        UUID userId = UUID.randomUUID();

        Date now = new Date();

        String expiredToken =
                Jwts.builder()
                        .subject(userId.toString())
                        .issuedAt(
                                new Date(now.getTime() - 10_000)
                        )
                        .expiration(
                                new Date(now.getTime() - 5_000)
                        )
                        .signWith(
                                Keys.hmacShaKeyFor(
                                        secret.getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                )
                        )
                        .compact();

        assertThrows(
                Exception.class,
                () -> jwtService.extractUserId(expiredToken)
        );
    }

    @Test
    void extractUserId_shouldThrowExceptionWhenTokenIsTampered() {

        UUID userId = UUID.randomUUID();

        String token =
                jwtService.generateAccessToken(userId);

        // Change part of the signature/payload
        String tamperedToken =
                token.substring(0, token.length() - 2) + "ab";

        assertThrows(
                Exception.class,
                () -> jwtService.extractUserId(tamperedToken)
        );
    }
}