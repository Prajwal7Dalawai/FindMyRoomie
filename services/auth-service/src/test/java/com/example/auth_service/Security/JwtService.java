package com.example.auth_service.Service.impl;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final long accessTokenExpiration;
    private final String issuer;
    private final String audience;

    public JwtService(
            @Value("${jwt.private-key}") Resource privateKeyResource,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.audience}") String audience
    ) throws Exception {

        this.privateKey = loadPrivateKey(privateKeyResource);
        this.accessTokenExpiration = accessTokenExpiration;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateAccessToken(UUID userId) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + accessTokenExpiration
        );

        return Jwts.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .audience().add(audience).and()
                .issuedAt(now)
                .expiration(expiration)
                .id(UUID.randomUUID().toString())
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    private PrivateKey loadPrivateKey(Resource resource)
            throws Exception {

        String key = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        key = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(decoded);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }
}