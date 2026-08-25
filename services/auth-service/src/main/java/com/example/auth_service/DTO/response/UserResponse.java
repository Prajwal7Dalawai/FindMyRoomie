package com.example.auth_service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean phoneVerified;
    private String status;
}