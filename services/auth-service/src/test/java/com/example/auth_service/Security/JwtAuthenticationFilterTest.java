package com.example.auth_service.Security;

import com.example.auth_service.Service.impl.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService);

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldContinueWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );
    }

    @Test
    void doFilterInternal_shouldContinueWhenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic some-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );
    }

    @Test
    void doFilterInternal_shouldAuthenticateWhenTokenIsValid()
            throws ServletException, IOException {

        UUID userId = UUID.randomUUID();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(jwtService.extractUserId("valid-token"))
                .thenReturn(userId);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                userId,
                authentication.getPrincipal()
        );

        assertTrue(authentication.isAuthenticated());

        verify(jwtService)
                .extractUserId("valid-token");

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticateWhenTokenIsInvalid()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer invalid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(jwtService.extractUserId("invalid-token"))
                .thenThrow(new JwtException("Invalid JWT"));

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .extractUserId("invalid-token");

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticateWhenTokenCausesIllegalArgumentException()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer malformed-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(jwtService.extractUserId("malformed-token"))
                .thenThrow(new IllegalArgumentException("Malformed token"));

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .extractUserId("malformed-token");

        verify(filterChain)
                .doFilter(request, response);
    }
}