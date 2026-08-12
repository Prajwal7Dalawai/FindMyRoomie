package com.example.auth_service.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String email;

    private String countryCode;

    private String phoneNumber;

    private String password;
}