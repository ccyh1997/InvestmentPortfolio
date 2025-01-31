package com.example.investmentportfolio.security;

import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.util.NotFoundException;
import com.example.investmentportfolio.util.ValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.investmentportfolio.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    @Mock
    private UserRepository userRepository;

    private JwtTokenProvider jwtTokenProvider;

    String mockJwtSecret = "AflGgAua0y93Xxn+S8TkjvPE1zz0Rz0OBH15C3PaLEg=";

    @BeforeEach
    void setup() {
        jwtTokenProvider = Mockito.spy(new JwtTokenProvider(userRepository, mockJwtSecret));
    }

    @Test
    void givenValidUsername_whenGenerateJwt_thenReturnJwt() {
        String username = "testUser";
        List<String> roles = List.of("USER");
        when(jwtTokenProvider.getSignInKey()).thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)));
        when(userRepository.findRolesByUsername(username.toUpperCase())).thenReturn(roles);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.of(1L));
        String jwt = jwtTokenProvider.generateJwt(username);
        assertNotNull(jwt);
        verify(userRepository, times(1)).findRolesByUsername(username.toUpperCase());
        verify(userRepository, times(1)).findIdByUsername(username.toUpperCase());
    }

    @Test
    void givenValidUsernameButInvalidSecretKey_whenGenerateJwt_thenThrowValidationException() {
        String username = "testUser";
        List<String> roles = List.of("USER");
        String errorMessage = "The key is not valid for the specified signing algorithm.";
        when(jwtTokenProvider.getSignInKey()).thenThrow(new IllegalArgumentException(errorMessage));
        when(userRepository.findRolesByUsername(username.toUpperCase())).thenReturn(roles);
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.of(1L));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            jwtTokenProvider.generateJwt(username);
        });
        assertEquals(INTERNAL_SERVER_ERROR_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(errorMessage, exception.getError().getErrorMessages().getFirst());
        verify(userRepository, times(1)).findRolesByUsername(username.toUpperCase());
        verify(userRepository, times(1)).findIdByUsername(username.toUpperCase());
    }

    @Test
    void givenInvalidUsername_whenGenerateJwt_thenThrowNotFoundException() {
        String username = "testUser";
        when(userRepository.findIdByUsername(username.toUpperCase())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            jwtTokenProvider.generateJwt(username);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with username: testUser", exception.getError().getErrorMessages().getFirst());
        verify(userRepository, times(1)).findIdByUsername(username.toUpperCase());
    }

    @Test
    void givenValidJwt_whenGetUsernameFromToken_thenReturnUsername() {
        String username = "testUser";
        String jwt = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)), Jwts.SIG.HS256)
                .compact();
        when(jwtTokenProvider.getSignInKey()).thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)));
        String retrievedUsername = jwtTokenProvider.getUsernameFromToken(jwt);
        assertEquals(username, retrievedUsername);
    }

    @Test
    void givenExpiredJwt_whenGetUsernameFromToken_thenReturnUsername() {
        String username = "testUser";
        String jwt = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)), Jwts.SIG.HS256)
                .compact();
        Header headers = mock(Header.class);
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(username);
        when(jwtTokenProvider.getSignInKey()).thenThrow(new ExpiredJwtException(headers, claims, any()));
        String retrievedUsername = jwtTokenProvider.getUsernameFromToken(jwt);
        assertEquals(username, retrievedUsername);
    }

    @Test
    void givenInvalidJwt_whenGetUsernameFromToken_thenThrowValidationException() {
        String username = "testUser";
        String jwt = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)), Jwts.SIG.HS256)
                .compact();
        String errorMessage = "Malformed JWT signature.";
        when(jwtTokenProvider.getSignInKey()).thenThrow(new MalformedJwtException("Malformed JWT signature."));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            jwtTokenProvider.getUsernameFromToken(jwt);
        });
        assertEquals(UNAUTHORIZED_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(errorMessage, exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidJwt_whenValidateToken_thenReturnTrue() {
        String jwt = Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)), Jwts.SIG.HS256)
                .compact();
        boolean isValidJwt = jwtTokenProvider.validateToken(jwt);
        assertTrue(isValidJwt);
    }

    @Test
    void givenInvalidJwt_whenValidateToken_thenThrowValidationException() {
        String jwt = Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)), Jwts.SIG.HS256)
                .compact();
        String errorMessage = "Malformed JWT signature.";
        when(jwtTokenProvider.getSignInKey()).thenThrow(new MalformedJwtException("Malformed JWT signature."));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            jwtTokenProvider.validateToken(jwt);
        });
        assertEquals(UNAUTHORIZED_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(errorMessage, exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequest_whenGetJwtFromRequest_thenReturnJwt() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(String.format("Bearer %s", jwt));
        String retrievedJwt = jwtTokenProvider.getJwtFromRequest(request);
        assertEquals(jwt, retrievedJwt);
    }

    @Test
    void givenInvalidRequestWithEmptyBearerToken_whenGetJwtFromRequest_thenReturnJwt() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("");
        String retrievedJwt = jwtTokenProvider.getJwtFromRequest(request);
        assertNull(retrievedJwt);
    }

    @Test
    void givenInvalidRequestWithInvalidBearerToken_whenGetJwtFromRequest_thenReturnJwt() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwic3ViIjoiY2N5aF85NyIsImlhdCI6MTczODA0MDAzMiwiZXhwIjoxNzM4MTI2NDMyfQ.HdUlruYlYmr6mWS9imGVq5ynIw5hi5JrLWP28anqceo";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(String.format("Bear %s", jwt));
        String retrievedJwt = jwtTokenProvider.getJwtFromRequest(request);
        assertNull(retrievedJwt);
    }

    @Test
    void givenValidJwt_whenExtractClaims_thenReturnClaims() {
        String jwt = Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)), Jwts.SIG.HS256)
                .compact();
        Claims extractedClaims = jwtTokenProvider.extractClaims(jwt);
        assertNotNull(extractedClaims);
    }

    @Test
    void givenInvalidJwt_whenExtractClaims_thenThrowValidationException() {
        String jwt = Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(mockJwtSecret)), Jwts.SIG.HS256)
                .compact();
        String errorMessage = "Malformed JWT signature.";
        when(jwtTokenProvider.getSignInKey()).thenThrow(new MalformedJwtException("Malformed JWT signature."));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            jwtTokenProvider.extractClaims(jwt);
        });
        assertEquals(UNAUTHORIZED_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(errorMessage, exception.getError().getErrorMessages().getFirst());
    }
}