package com.example.auth_service.Controller;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.request.RefreshTokenRequest;
import com.example.auth_service.DTO.request.RegisterRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.Models.RefreshToken;
import com.example.auth_service.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return  ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody RefreshTokenRequest request){
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @GetMapping("/oauth2/success")
    public AuthResponse oauth2Success(
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        return authService.loginWithGoogle(oauth2User);
    }
}
