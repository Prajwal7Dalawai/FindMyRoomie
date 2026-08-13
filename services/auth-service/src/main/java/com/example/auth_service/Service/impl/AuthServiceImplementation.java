package com.example.auth_service.Service.impl;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.request.RegisterRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.Service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request){
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request){
        return null;
    }

    @Override
    public void logout(String refreshToken){

    }

    @Override
    public AuthResponse refreshToken(String refreshToken){
        return null;
    }
}
