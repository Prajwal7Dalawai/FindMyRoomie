package com.example.auth_service.Service.impl;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.request.RegisterRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.DTO.response.UserResponse;
import com.example.auth_service.Models.AuthIdentity;
import com.example.auth_service.Models.RefreshToken;
import com.example.auth_service.Models.User;
import com.example.auth_service.Repository.AuthIdentityRepository;
import com.example.auth_service.Repository.RefreshTokenRepository;
import com.example.auth_service.Repository.UserRepository;
import com.example.auth_service.Service.AuthService;
import com.example.auth_service.Service.exception.InvalidCredentialsException;
import com.example.auth_service.Service.exception.ResourceAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

        private final PasswordEncoder passwordEncoder;
        private final UserRepository userRepository;
        private final AuthIdentityRepository authIdentityRepository;
        private final JwtService jwtService;
        private final RefreshTokenService refreshTokenService;
        private final RefreshTokenRepository refreshTokenRepository;


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
        @Transactional
        public AuthResponse login(LoginRequest request){

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

            AuthIdentity identity = authIdentityRepository.findByUserIdAndProvider(user.getId(),"LOCAL")
                    .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

            if(!passwordEncoder.matches(request.getPassword(),identity.getPasswordHash())){
                throw   new InvalidCredentialsException("Invalid password");
            }

            String accessToken = jwtService.generateAccessToken(user.getId());

            String refreshToken = refreshTokenService.generateRefreshToken();
            String refreshTokenHash = refreshTokenService.hashToken(refreshToken);

            RefreshToken refreshTokenEntity = new  RefreshToken();
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setTokenHash(refreshTokenHash);
            refreshTokenEntity.setRevoked(false);
            refreshTokenEntity.setCreatedAt(OffsetDateTime.now());
            refreshTokenEntity.setExpiresAt(
                    OffsetDateTime.now().plusDays(30)
            );
            refreshTokenRepository.save(refreshTokenEntity);

            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.isEmailVerified(),
                    user.isPhoneVerified(),
                    user.getStatus()
            );
            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    900,
                    userResponse
            );
        }

        @Override
        @Transactional
        public void logout(String refreshToken){
            String tokenHash = refreshTokenService.hashToken(refreshToken);
            RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
                    .orElseThrow(()-> new InvalidCredentialsException("Invalid refresh token"));
            refreshTokenEntity.setRevoked(true);
            refreshTokenRepository.save(refreshTokenEntity);
        }

        @Override
        public AuthResponse refreshToken(String refreshToken){
            String tokenHash = refreshTokenService.hashToken(refreshToken);
            RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
                    .orElseThrow(()-> new InvalidCredentialsException("Invalid refresh token"));

            if(refreshTokenEntity.isRevoked()){
                throw   new InvalidCredentialsException("refresh Token has been revoked");
            }

            if(refreshTokenEntity.getExpiresAt().isBefore(OffsetDateTime.now())){
                throw   new InvalidCredentialsException("refresh Token has expired");
            }

            User user = refreshTokenEntity.getUser();
            String accessToken = jwtService.generateAccessToken(user.getId());
            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.isEmailVerified(),
                    user.isPhoneVerified(),
                    user.getStatus()
            );

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    900,
                    userResponse
            );
        }

       @Transactional
       @Override
       public void logoutAll(UUID userId){
        List<RefreshToken> refreshTokenList = refreshTokenRepository.findAllByUserIdAndRevokedFalse(userId);
        for(RefreshToken refreshToken : refreshTokenList){
            refreshToken.setRevoked(true);
        }
           refreshTokenRepository.saveAll(refreshTokenList);
    }

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(OAuth2User oauth2User) {

        // Get Google's user information
        String googleUserId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");

        Boolean emailVerified =
                oauth2User.getAttribute("email_verified");

        if (googleUserId == null || email == null) {
            throw new InvalidCredentialsException(
                    "Google account information is incomplete"
            );
        }

        User user;

        // --------------------------------------------------
        // 1. Check whether this Google identity already exists
        // --------------------------------------------------

        Optional<AuthIdentity> existingIdentity =
                authIdentityRepository.findByProviderAndProviderUserId(
                        "GOOGLE",
                        googleUserId
                );

        if (existingIdentity.isPresent()) {

            // Google account already linked
            user = existingIdentity.get().getUser();

        } else {

            // --------------------------------------------------
            // 2. Google identity doesn't exist.
            //    Check whether this email already exists.
            // --------------------------------------------------

            Optional<User> existingUser =
                    userRepository.findByEmail(email);

            if (existingUser.isPresent()) {

                // Existing account found.
                // Link Google to this existing user.
                user = existingUser.get();

            } else {

                // --------------------------------------------------
                // 3. Completely new user
                // --------------------------------------------------

                user = new User();

                user.setEmail(email);
                user.setEmailVerified(
                        Boolean.TRUE.equals(emailVerified)
                );
                user.setPhoneVerified(false);
                user.setStatus("ACTIVE");

                user.setCreatedAt(OffsetDateTime.now());
                user.setUpdatedAt(OffsetDateTime.now());

                user = userRepository.save(user);
            }

            // --------------------------------------------------
            // 4. Create Google AuthIdentity
            // --------------------------------------------------

            AuthIdentity googleIdentity = new AuthIdentity();

            googleIdentity.setUser(user);
            googleIdentity.setProvider("GOOGLE");
            googleIdentity.setProviderUserId(googleUserId);
            googleIdentity.setPasswordHash(null);
            googleIdentity.setCreatedAt(OffsetDateTime.now());
            googleIdentity.setUpdatedAt(OffsetDateTime.now());

            authIdentityRepository.save(googleIdentity);
        }

        // --------------------------------------------------
        // 5. Generate OUR access token
        // --------------------------------------------------

        String accessToken =
                jwtService.generateAccessToken(user.getId());

        // --------------------------------------------------
        // 6. Generate OUR refresh token
        // --------------------------------------------------

        String refreshToken =
                refreshTokenService.generateRefreshToken();

        String refreshTokenHash =
                refreshTokenService.hashToken(refreshToken);

        // --------------------------------------------------
        // 7. Store refresh token hash in database
        // --------------------------------------------------

        RefreshToken refreshTokenEntity =
                new RefreshToken();

        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenHash(refreshTokenHash);
        refreshTokenEntity.setRevoked(false);
        refreshTokenEntity.setCreatedAt(OffsetDateTime.now());
        refreshTokenEntity.setExpiresAt(
                OffsetDateTime.now().plusDays(30)
        );

        refreshTokenRepository.save(refreshTokenEntity);

        // --------------------------------------------------
        // 8. Build UserResponse
        // --------------------------------------------------

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.getStatus()
        );

        // --------------------------------------------------
        // 9. Return the SAME response structure as normal login
        // --------------------------------------------------

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenExpiration(),
                userResponse
        );
    }

}
