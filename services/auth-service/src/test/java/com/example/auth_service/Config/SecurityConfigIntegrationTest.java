package com.example.auth_service.Config;

import com.example.auth_service.DTO.request.LoginRequest;
import com.example.auth_service.DTO.response.AuthResponse;
import com.example.auth_service.DTO.response.UserResponse;
import com.example.auth_service.Service.AuthService;
import com.example.auth_service.Service.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;


    @Test
    void publicLoginEndpoint_shouldBeAccessibleWithoutAuthentication()
            throws Exception {

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
                                            "password": "anything"
                                        }
                                        """)
                )
                .andExpect(status().isOk());
    }


    @Test
    void protectedEndpoint_shouldReturnUnauthorizedWithoutToken()
            throws Exception {

        mockMvc.perform(
                        get("/api/auth/some-protected-endpoint")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void protectedEndpoint_shouldReturnUnauthorizedWithInvalidToken()
            throws Exception {

        mockMvc.perform(
                        get("/api/auth/some-protected-endpoint")
                                .header(
                                        "Authorization",
                                        "Bearer invalid-token"
                                )
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void protectedEndpoint_shouldAllowValidToken()
            throws Exception {

        UUID userId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId);

        mockMvc.perform(
                        get("/api/auth/test")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }
}