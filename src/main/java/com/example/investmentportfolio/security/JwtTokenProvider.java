package com.example.investmentportfolio.security;

import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.util.Constants;
import com.example.investmentportfolio.util.CustomError;
import com.example.investmentportfolio.util.ValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtTokenProvider {
    private static final Logger LOGGER = LogManager.getLogger(JwtTokenProvider.class);
    private final UserRepository userRepository;
    private final String jwtSecret;

    public JwtTokenProvider(UserRepository userRepository, @Value("${jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.jwtSecret = jwtSecret;
    }

    public String generateJwt(String username) {

        long jwtExpirationMs = (long) 15 * 60 * 1000; // 15 minutes expiry time
        List<String> roles = userRepository.findRolesByUsername(username.toUpperCase());
        List<String> roleList = Arrays.asList(roles.getFirst().split(","));
        List<String> prefixedRoles = roleList.stream()
                .map(role -> "ROLE_" + role)
                .toList();
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("roles", prefixedRoles);
        try {
            return Jwts
                    .builder()
                    .claims(claims) // public or private claims
                    .subject(username) // registered claim
                    .issuedAt(new Date(System.currentTimeMillis())) // registered claim
                    .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // registered claim
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            List<String> errorMessage = Collections.singletonList(e.getMessage());
            LOGGER.error(errorMessage);
            throw new ValidationException(new CustomError(Constants.INTERNAL_SERVER_ERROR_ERROR_CODE, errorMessage));
        }
    }


    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return claims.getSubject();
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            List<String> errorMessage = Collections.singletonList(e.getMessage());
            LOGGER.error(errorMessage);
            throw new ValidationException(new CustomError(Constants.UNAUTHORIZED_ERROR_CODE, errorMessage));
        }
    }

    public boolean validateToken(String jwt) {
        try {
            Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(jwt);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            List<String> errorMessage = Collections.singletonList(e.getMessage());
            LOGGER.error(errorMessage);
            throw new ValidationException(new CustomError(Constants.UNAUTHORIZED_ERROR_CODE, errorMessage));
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims extractClaims(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            List<String> errorMessage = Collections.singletonList(e.getMessage());
            LOGGER.error(errorMessage);
            throw new ValidationException(new CustomError(Constants.UNAUTHORIZED_ERROR_CODE, errorMessage));
        }
    }
}