package com.example.auth_service.Repository;

import com.example.auth_service.Models.AuthIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, UUID> {
    Optional<AuthIdentity> findByProviderAndProviderUserId(
            String provider,
            String providerUserId
    );

    Boolean existsByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<AuthIdentity> findByUserIdAndProvider(
            UUID userId,
            String provider
    );

}
