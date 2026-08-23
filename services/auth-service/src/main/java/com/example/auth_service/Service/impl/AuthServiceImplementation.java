package com.example.auth_service.Service.impl;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.request.RegisterRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.Models.AuthIdentity;
import com.example.auth_service.Models.User;
import com.example.auth_service.Repository.AuthIdentityRepository;
import com.example.auth_service.Repository.UserRepository;
import com.example.auth_service.Service.AuthService;
import com.example.auth_service.Service.exception.InvalidCredentialsException;
import com.example.auth_service.Service.exception.ResourceAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthIdentityRepository authIdentityRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        if(request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new ResourceAlreadyExistsException("Phone number already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPhoneVerified(false);
        user.setEmailVerified(false);
        user.setStatus("ACTIVE");

        OffsetDateTime now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);

        AuthIdentity identity = new AuthIdentity();
        identity.setUser(user);
        identity.setProvider("LOCAL");
        identity.setProviderUserId(request.getEmail());

        identity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        identity.setCreatedAt(now);
        identity.setUpdatedAt(now);
        authIdentityRepository.save(identity);

        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

        AuthIdentity identity = authIdentityRepository.findByUserIdAndProvider(user.getId(),"LOCAL")
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(),identity.getPasswordHash())){
            throw   new InvalidCredentialsException("Invalid password");
        }

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
