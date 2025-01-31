package com.example.investmentportfolio.security;

import com.example.investmentportfolio.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenValidRequest_whenRegisterUser_thenReturnOk() throws Exception {
        UserDto requestUserDto = new UserDto("testuser", "testpassword", null, null, null, null, null);
        when(authenticationService.registerUser("testuser", "testpassword")).thenReturn("You’re all set! Your registration is complete, and you can proceed to log in.");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("You’re all set! Your registration is complete, and you can proceed to log in."));
        verify(authenticationService, times(1)).registerUser(any(), any());
    }

    @Test
    void givenInvalidRequest_whenRegisterUser_thenReturnBadRequest() throws Exception {
        UserDto requestUserDto = new UserDto("testuser", "test", null, null, null, null, null);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Password must be at least 8 characters long and contain no whitespace characters."));
    }

    @Test
    void givenValidRequest_whenLogin_thenReturnOk() throws Exception {
        UserDto requestUserDto = new UserDto("testuser", "testpassword", null, null, null, null, null);
        AuthenticationDto responseAuthenticationDto = new AuthenticationDto("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzYzMDksImV4cCI6MTczNzM3NzIwOX0.dPngL6I917bXHEKfF181Y541C62qMhnM0Oey59q66ew", UUID.fromString("b4b788b2-989a-4b4a-8631-12f94460ff2a"));
        when(authenticationService.generateTokens("testuser", "testpassword")).thenReturn(responseAuthenticationDto);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzYzMDksImV4cCI6MTczNzM3NzIwOX0.dPngL6I917bXHEKfF181Y541C62qMhnM0Oey59q66ew"))
                .andExpect(jsonPath("$.refreshToken").value("b4b788b2-989a-4b4a-8631-12f94460ff2a"));
        verify(authenticationService, times(1)).generateTokens(any(), any());
    }

    @Test
    void givenInvalidRequest_whenLogin_thenReturnBadRequest() throws Exception {
        UserDto requestUserDto = new UserDto("testuser", "test", null, null, null, null, null);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Password must be at least 8 characters long and contain no whitespace characters."));
    }

    @Test
    void givenValidRequest_whenRefresh_thenReturnOk() throws Exception {
        AuthenticationDto requestAuthenticationDto = new AuthenticationDto("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzYzMDksImV4cCI6MTczNzM3NzIwOX0.dPngL6I917bXHEKfF181Y541C62qMhnM0Oey59q66ew", UUID.fromString("b4b788b2-989a-4b4a-8631-12f94460ff2a"));
        AuthenticationDto responseAuthenticationDto = new AuthenticationDto("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzY1NjUsImV4cCI6MTczNzM3NzQ2NX0.odp-y0_PZ97ZWOpVLN5wyvf4rXS1p6fEuBPUoEOYKgY", null);
        when(authenticationService.regenerateJwt(any(), any())).thenReturn(responseAuthenticationDto);
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAuthenticationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzY1NjUsImV4cCI6MTczNzM3NzQ2NX0.odp-y0_PZ97ZWOpVLN5wyvf4rXS1p6fEuBPUoEOYKgY"));
        verify(authenticationService, times(1)).regenerateJwt(any(), any());
    }

    @Test
    void givenInvalidRequest_whenRefresh_thenReturnBadRequest() throws Exception {
        AuthenticationDto requestAuthenticationDto = new AuthenticationDto("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzYzMDksImV4cCI6MTczNzM3NzIwOX0.dPngL6I917bXHEKfF181Y541C62qMhnM0Oey59q66ew", null);
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAuthenticationDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Refresh token must be provided."));
    }
}