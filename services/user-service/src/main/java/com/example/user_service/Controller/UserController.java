package com.example.user_service.Controller;

import com.example.user_service.DTO.Request.UserProfileRequest;
import com.example.user_service.DTO.Response.UserProfileResponse;
import com.example.user_service.Service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/users/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserProfileService userProfileService;

    @GetMapping
    public UserProfileResponse getProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        return userProfileService.getProfile(userId);
    }

    @PostMapping
    public UserProfileResponse createProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserProfileRequest request
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        return userProfileService.createProfile(
                userId,
                request
        );
    }

    @PutMapping
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserProfileRequest request
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        return userProfileService.updateProfile(
                userId,
                request
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        userProfileService.deleteProfile(userId);

        return ResponseEntity.noContent().build();
    }
}
