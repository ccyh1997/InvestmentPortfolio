package com.example.investmentportfolio.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "DTO representing authentication details.")
public class AuthenticationDto {

    @Schema(description = "JWT Token for authentication", example = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3Mzc3ODUxMDYsImV4cCI6MTczNzc4NjAwNn0.0eXX3BaZHkI5ulLSDJb5TUGL2RcbOh3eh42y_NYfEbQ")
    private String jwt;

    @Schema(description = "Refresh token in UUID format", example = "434b5ff0-33d1-45c9-aad3-f4fcab4c4194")
    private UUID refreshToken;
}