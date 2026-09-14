package com.example.auth_service.Services;
import com.example.auth_service.Repository.AuthIdentityRepository;
import com.example.auth_service.Repository.RefreshTokenRepository;
import com.example.auth_service.Repository.UserRepository;
import com.example.auth_service.Service.impl.RefreshTokenService;
import com.example.auth_service.Service.impl.AuthServiceImplementation;
import com.example.auth_service.Service.impl.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
abstract class AuthServiceImplementationTest {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected AuthIdentityRepository authIdentityRepository;

    @Mock
    protected RefreshTokenRepository refreshTokenRepository;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    protected JwtService jwtService;

    @Mock
    protected RefreshTokenService refreshTokenService;

    @InjectMocks
    protected AuthServiceImplementation authService;

    @BeforeEach
    void setUp() {
        // Test-specific setup will go here
    }
}
