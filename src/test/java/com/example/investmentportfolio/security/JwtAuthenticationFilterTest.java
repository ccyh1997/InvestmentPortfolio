package com.example.investmentportfolio.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private JwtAuthenticationFilter  jwtAuthenticationFilter;

    @Test
    void givenValidJwt_whenDoFilterInternal_thenGenerateAuthentication() throws ServletException, IOException {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        Object roles = List.of("ROLE_USER");
        Claims claims = mock(Claims.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(jwtTokenProvider.getJwtFromRequest(any())).thenReturn(jwt);
        when(jwtTokenProvider.validateToken(jwt)).thenReturn(true);
        when(jwtTokenProvider.extractClaims(jwt)).thenReturn(claims);
        when(claims.get("roles")).thenReturn(roles);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtTokenProvider, times(1)).getJwtFromRequest(request);
        verify(jwtTokenProvider, times(1)).validateToken(jwt);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void givenInvalidJwt_whenDoFilterInternal_thenDoNothing() throws ServletException, IOException {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(jwtTokenProvider.getJwtFromRequest(any())).thenReturn(jwt);
        when(jwtTokenProvider.validateToken(jwt)).thenReturn(false);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtTokenProvider, times(1)).getJwtFromRequest(request);
        verify(jwtTokenProvider, times(1)).validateToken(jwt);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void givenNullJwt_whenDoFilterInternal_thenDoNothing() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(jwtTokenProvider.getJwtFromRequest(any())).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtTokenProvider, times(1)).getJwtFromRequest(request);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}