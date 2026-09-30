package com.example.auth_service.Controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@RestController
public class JwksController {

    private final RSAPublicKey publicKey;

    public JwksController(
            @Value("${jwt.public-key}") Resource publicKeyResource
    ) throws Exception {

        this.publicKey = loadPublicKey(publicKeyResource);
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() throws JOSEException {

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .keyID("findmyroomie-key-1")
                .build();

        return new JWKSet(rsaKey).toJSONObject();
    }

    private RSAPublicKey loadPublicKey(
            Resource resource
    ) throws Exception {

        String key = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        key = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(decoded);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
}