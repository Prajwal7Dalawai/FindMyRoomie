package com.example.user_service.Service.implementation;

import com.example.user_service.DTO.Request.UserProfileRequest;
import com.example.user_service.DTO.Response.UserProfileResponse;
import com.example.user_service.Exception.ProfileAlreadyExistsException;
import com.example.user_service.Exception.ProfileNotFoundException;
import com.example.user_service.Model.UserProfile;
import com.example.user_service.Repository.UserProfileRepository;
import com.example.user_service.Service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileImplementation implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    @Override
    public UserProfileResponse getProfile(UUID userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(()-> new ProfileNotFoundException("Profile with " + userId + " not found"));
        return mapToResponse(profile);
    }

    @Override
    public UserProfileResponse createProfile(UUID userId, UserProfileRequest request) {
        if(userProfileRepository.existsById(userId)) {
            throw new ProfileAlreadyExistsException("Profile with " + userId + " already exists");
        }
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setBio(request.getBio());
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());
        profile.setCity(request.getCity());

        OffsetDateTime now = OffsetDateTime.now();

        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);

        UserProfile savedProfile =
                userProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    @Override
    public UserProfileResponse updateProfile(UUID userId, UserProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(()-> new ProfileNotFoundException("Profile with " + userId + " not found"));

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setBio(request.getBio());
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());
        profile.setCity(request.getCity());

        profile.setUpdatedAt(OffsetDateTime.now());

        UserProfile updatedProfile =
                userProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    @Override
    public void deleteProfile(UUID userId) {
        if (!userProfileRepository.existsById(userId)) {
            throw new ProfileNotFoundException("Profile with " + userId + " not found");
        }

        userProfileRepository.deleteById(userId);
    }

    private UserProfileResponse mapToResponse(UserProfile profile) {

        return new UserProfileResponse(
                profile.getUserId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDateOfBirth(),
                profile.getGender(),
                profile.getBio(),
                profile.getProfilePhotoUrl(),
                profile.getCity(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
