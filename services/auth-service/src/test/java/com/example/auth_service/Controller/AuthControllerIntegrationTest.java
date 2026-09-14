package com.example.auth_service.Controller;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.request.RefreshTokenRequest;
import com.example.auth_service.DTO.request.RegisterRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.DTO.response.UserResponse;
import com.example.auth_service.Service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;


    // ---------------------------------------------------------
    // REGISTER
    // ---------------------------------------------------------

    @Test
    void register_shouldReturnCreated() throws Exception {

        UUID userId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse(
                userId,
                "test@example.com",
                "9876543210",
                false,
                false,
                "ACTIVE"
        );

        AuthResponse authResponse = new AuthResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                900L,
                userResponse
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "email": "test@example.com",
                                        "countryCode": "+91",
                                        "phoneNumber": "9876543210",
                                        "password": "Password@123"
                                    }
                                    """)
                )
                .andExpect(status().isCreated());
    }


    // ---------------------------------------------------------
    // LOGIN
    // ---------------------------------------------------------

    @Test
    void login_shouldReturnOk() throws Exception {

        UUID userId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse(
                userId,
                "test@example.com",
                null,
                false,
                false,
                "ACTIVE"
        );

        AuthResponse authResponse = new AuthResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                900L,
                userResponse
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "test@example.com",
                                            "password": "Password@123"
                                        }
                                        """)
                )
                .andExpect(status().isOk());
    }


    // ---------------------------------------------------------
    // REFRESH TOKEN
    // ---------------------------------------------------------

    @Test
    void refreshToken_shouldReturnOk() throws Exception {

        UUID userId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse(
                userId,
                "test@example.com",
                null,
                false,
                false,
                "ACTIVE"
        );

        AuthResponse authResponse = new AuthResponse(
                "new-access-token",
                "refresh-token",
                "Bearer",
                900L,
                userResponse
        );

        when(authService.refreshToken(any(String.class)))
                .thenReturn(authResponse);

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "refreshToken": "refresh-token"
                                        }
                                        """)
                )
                .andExpect(status().isOk());
    }


    // ---------------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------------

    @Test
    void logout_shouldReturnNoContent() throws Exception {

        doNothing()
                .when(authService)
                .logout(any(String.class));

        mockMvc.perform(
                        post("/api/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "refreshToken": "refresh-token"
                                        }
                                        """)
                )
                .andExpect(status().isNoContent());
    }
}