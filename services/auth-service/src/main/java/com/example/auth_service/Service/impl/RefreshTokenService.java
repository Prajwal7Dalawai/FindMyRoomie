package com.example.auth_service.Service.impl;

import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public String hashToken(String token) {

        try {

            var digest = java.security.MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            return java.util.HexFormat.of().formatHex(hash);

        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
