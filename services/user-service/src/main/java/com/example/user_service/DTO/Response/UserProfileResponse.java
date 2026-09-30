package com.example.user_service.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bio;
    private String profilePhotoUrl;
    private String city;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
