package com.example.auth_service.Service;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.request.RegisterRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.Models.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String refreshToken);
    void logoutAll(UUID userId);
    AuthResponse refreshToken(String refreshToken);
    AuthResponse loginWithGoogle(OAuth2User oauth2User);
}
