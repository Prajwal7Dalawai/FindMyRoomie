package com.example.user_service.Service;


import com.example.user_service.DTO.Request.UserProfileRequest;
import com.example.user_service.DTO.Response.UserProfileResponse;

import java.util.UUID;

public interface UserProfileService {

    UserProfileResponse getProfile(UUID userId);
    UserProfileResponse updateProfile(UUID userId, UserProfileRequest request);
    UserProfileResponse createProfile(UUID userId, UserProfileRequest request);
    void deleteProfile(UUID userId);
}
