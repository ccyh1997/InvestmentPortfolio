package com.example.investmentportfolio.security;

import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.util.NotFoundException;
import com.example.investmentportfolio.util.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static com.example.investmentportfolio.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void givenValidUsername_whenRegisterUser_thenReturnSuccessfullyCreatedMessage() {
        when(userRepository.existsByUsernameIgnoreCase(any())).thenReturn(false);
        assertEquals(SUCCESSFUL_REGISTRATION_MESSAGE, authenticationService.registerUser("testUser", "testPassword"));
        verify(userRepository, times(1)).existsByUsernameIgnoreCase((any()));
    }

    @Test
    void givenInvalidUsername_whenRegisterUser_thenThrowValidationException() {
        when(userRepository.existsByUsernameIgnoreCase(any())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authenticationService.registerUser("testUser", "testPassword");
        });
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(USERNAME_TAKEN_ERROR_MESSAGE, exception.getError().getErrorMessages().getFirst());
        verify(userRepository, times(1)).existsByUsernameIgnoreCase((any()));
    }

    @Test
    void givenValidUsernameAndPassword_whenGenerateTokens_thenReturnSuccessfullyCreatedMessage() {
        String username = "testUser";
        String password = "testPassword";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        UUID refreshToken = UUID.fromString("ebb03717-f841-4468-8d23-7472f411c07e");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
        when(jwtTokenProvider.generateJwt(username)).thenReturn(jwt);
        when(refreshTokenProvider.generateRefreshToken(authentication)).thenReturn(refreshToken);
        AuthenticationDto authenticationDto = authenticationService.generateTokens(username, password);
        assertEquals(jwt, authenticationDto.getJwt());
        assertEquals(refreshToken, authenticationDto.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate((usernamePasswordAuthenticationToken));
        verify(jwtTokenProvider, times(1)).generateJwt((username));
        verify(refreshTokenProvider, times(1)).generateRefreshToken((authentication));
    }

    @Test
    void givenInvalidUsernameOrPassword_whenGenerateTokens_thenThrowValidationException() {
        String username = "testUser";
        String password = "testPassword";
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(any()));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authenticationService.generateTokens(username, password);
        });
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(INVALID_CREDENTIALS_ERROR_MESSAGE, exception.getError().getErrorMessages().getFirst());
        verify(authenticationManager, times(1)).authenticate((usernamePasswordAuthenticationToken));
    }

    @Test
    void givenValidUsernameAndRefreshToken_whenRegenerateJwt_thenReturnNewJwt() {
        String username = "testUser";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        UUID refreshToken =  UUID.fromString("ebb03717-f841-4468-8d23-7472f411c07e");
        String newJwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODIxOTkzMiwiZXhwIjoxNzM4MzA2MzMyfQ.23PLkUYdywvFt-A5dPxPtjOECTAw2Su0Y2hcI2KaA_w";
        when(jwtTokenProvider.getUsernameFromToken(jwt)).thenReturn(username);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.of(1L));
        when(tokenRepository.countValidRefreshTokens(1L, refreshToken)).thenReturn(1);
        when(jwtTokenProvider.generateJwt(username)).thenReturn(newJwt);
        AuthenticationDto authenticationDto = authenticationService.regenerateJwt(jwt, refreshToken);
        assertEquals(newJwt, authenticationDto.getJwt());
        verify(jwtTokenProvider, times(1)).getUsernameFromToken((jwt));
        verify(userRepository, times(1)).findIdByUsername((username.toUpperCase()));
        verify(tokenRepository, times(1)).countValidRefreshTokens(1L, refreshToken);
        verify(jwtTokenProvider, times(1)).generateJwt((username));
    }

    @Test
    void givenInvalidUsername_whenRegenerateJwt_thenThrowNotFoundException() {
        String username = "testUser";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        UUID refreshToken = UUID.fromString("ebb03717-f841-4468-8d23-7472f411c07e");
        when(jwtTokenProvider.getUsernameFromToken(jwt)).thenReturn(username);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                authenticationService.regenerateJwt(jwt, refreshToken)
        );
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with username: testUser", exception.getError().getErrorMessages().getFirst());
        verify(jwtTokenProvider, times(1)).getUsernameFromToken(jwt);
        verify(userRepository, times(1)).findIdByUsername((username.toUpperCase()));
    }

    @Test
    void givenValidUsernameButExpiredRefreshToken_whenRegenerateJwt_thenThrowUnauthorized() {
        String username = "testUser";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        UUID refreshToken = UUID.fromString("ebb03717-f841-4468-8d23-7472f411c07e");
        when(jwtTokenProvider.getUsernameFromToken(jwt)).thenReturn(username);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.of(1L));
        when(tokenRepository.countValidRefreshTokens(1L, refreshToken)).thenReturn(0);
        ValidationException exception = assertThrows(ValidationException.class, () ->
                authenticationService.regenerateJwt(jwt, refreshToken)
        );
        assertEquals(UNAUTHORIZED_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(SESSION_EXPIRED_MESSAGE, exception.getError().getErrorMessages().getFirst());
        verify(jwtTokenProvider, times(1)).getUsernameFromToken((jwt));
        verify(userRepository, times(1)).findIdByUsername((username.toUpperCase()));
        verify(tokenRepository, times(1)).countValidRefreshTokens(1L, refreshToken);
    }
}