package com.example.user_service.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max=100, message = "First name should not exceed 100 characters")
    private String firstName;

    @Size(max=100, message = "Last name should not exceed 100 characters")
    private String lastName;

    @Past(message = "Date of birth should be in the past")
    private LocalDate dateOfBirth;

    @Size(max=30, message = "gender should not exceed 30 characters")
    private String gender;

    @Size(max = 1000, message = "Bio should not exceed 1000 charcters")
    private String bio;

    @Size(max=500, message = "URL should not exceed 500 charcters")
    private String profilePhotoUrl;

    @Size(max=100, message = "City should not exceed 100 charcters")
    private String city;
}
