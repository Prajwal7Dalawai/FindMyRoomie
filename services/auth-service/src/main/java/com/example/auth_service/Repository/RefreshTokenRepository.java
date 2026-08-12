package com.example.auth_service.Repository;

import com.example.auth_service.Models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findByUserId(UUID userId);
    List<RefreshToken> findAllByUserIdAndRevokedFalse(UUID userId);
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
