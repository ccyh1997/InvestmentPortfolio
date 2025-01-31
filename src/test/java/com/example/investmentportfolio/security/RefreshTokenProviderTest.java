package com.example.investmentportfolio.security;

import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.util.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.investmentportfolio.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenProviderTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private RefreshTokenProvider refreshTokenProvider;

    @Test
    void givenValidUsernameAndValidRefreshTokenIsNotPresentAndGeneratedRefreshTokenDoesNotAlreadyExist_whenGenerateRefreshToken_thenReturnNewRefreshToken() {
        String username = "testUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.of(1L));
        when(tokenRepository.getValidRefreshToken(1L)).thenReturn(null);
        when(tokenRepository.existsByRefreshToken(any())).thenReturn(false);
        UUID newRefreshToken = refreshTokenProvider.generateRefreshToken(authentication);
        assertNotNull(newRefreshToken, "Refresh token should not be null");
        verify(userRepository, times(1)).findIdByUsername(username.toUpperCase());
        verify(tokenRepository, times(1)).getValidRefreshToken(1L);
        verify(tokenRepository, times(1)).existsByRefreshToken(any());
    }

    @Test
    void givenValidUsernameAndValidRefreshTokenIsNotPresentAndGeneratedRefreshTokenAlreadyExists_whenGenerateRefreshToken_thenReturnNewRefreshToken() {
        String username = "testUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.of(1L));
        when(tokenRepository.getValidRefreshToken(1L)).thenReturn(null);
        AtomicInteger counter = new AtomicInteger(0);
        when(tokenRepository.existsByRefreshToken(any())).thenAnswer(invocation -> counter.getAndIncrement() < 3);
        UUID newRefreshToken = refreshTokenProvider.generateRefreshToken(authentication);
        assertNotNull(newRefreshToken, "Refresh token should not be null");
        verify(userRepository, times(1)).findIdByUsername(username.toUpperCase());
        verify(tokenRepository, times(1)).getValidRefreshToken(1L);
        verify(tokenRepository, times(4)).existsByRefreshToken(any());
    }

    @Test
    void givenValidUsernameAndValidRefreshTokenIsPresent_whenGenerateRefreshToken_thenReturnNewRefreshToken() {
        String username = "testUser";
        UUID refreshToken = UUID.fromString("ebb03717-f841-4468-8d23-7472f411c07e");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.of(1L));
        when(tokenRepository.getValidRefreshToken(1L)).thenReturn(refreshToken);
        UUID newRefreshToken = refreshTokenProvider.generateRefreshToken(authentication);
        assertEquals(refreshToken, newRefreshToken);
        verify(userRepository, times(1)).findIdByUsername(username.toUpperCase());
        verify(tokenRepository, times(1)).getValidRefreshToken(1L);
    }

    @Test
    void givenInvalidUsername_whenGenerateRefreshToken_thenThrowNotFoundException() {
        String username = "testUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            refreshTokenProvider.generateRefreshToken(authentication);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with username: testUser", exception.getError().getErrorMessages().getFirst());
        verify(userRepository, times(1)).findIdByUsername(username.toUpperCase());
    }
}