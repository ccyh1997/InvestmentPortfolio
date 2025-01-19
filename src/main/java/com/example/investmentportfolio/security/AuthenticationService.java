package com.example.investmentportfolio.security;

import java.util.UUID;

public interface AuthenticationService {
    String registerUser(String username, String password);
    AuthenticationDto generateTokens(String username, String password);
    AuthenticationDto regenerateJwt(String expiredJwt, UUID refreshToken);
}
