package com.example.investmentportfolio.security;

import com.example.investmentportfolio.model.User;
import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.util.Constants;
import com.example.investmentportfolio.util.CustomError;
import com.example.investmentportfolio.util.NotFoundException;
import com.example.investmentportfolio.util.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.investmentportfolio.util.Constants.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationServiceImpl.class);
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    public AuthenticationServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RefreshTokenProvider refreshTokenProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
    }

    @Override
    public String registerUser(String username, String password) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            List<String> errorMessage = Collections.singletonList(USERNAME_TAKEN_ERROR_MESSAGE);
            LOGGER.error(errorMessage);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessage));
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(Set.of(USER));
            userRepository.save(user);
            return SUCCESSFUL_REGISTRATION_MESSAGE;
        }
    }

    @Override
    public AuthenticationDto generateTokens(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String jwt = jwtTokenProvider.generateJwt(username);
            UUID refreshToken = refreshTokenProvider.generateRefreshToken(authentication);
            return new AuthenticationDto(jwt, refreshToken);
        } catch (BadCredentialsException e) {
            List<String> errorMessage = Collections.singletonList(INVALID_CREDENTIALS_ERROR_MESSAGE);
            LOGGER.error(errorMessage);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessage));
        }
    }

    @Override
    public AuthenticationDto regenerateJwt(String expiredJwt, UUID refreshToken) {
        String username = jwtTokenProvider.getUsernameFromToken(expiredJwt);
        Optional<Long> optionalUserId = userRepository.findIdByUsername(username.toUpperCase());
        if (optionalUserId.isPresent()) {
            Long userId = optionalUserId.get();
            Integer refreshTokenCount = tokenRepository.countValidRefreshTokens(userId, refreshToken);
            if (refreshTokenCount > 0) {
                String newJwt = jwtTokenProvider.generateJwt(username);
                AuthenticationDto authenticationDto = new AuthenticationDto();
                authenticationDto.setJwt(newJwt);
                return authenticationDto;
            } else {
                List<String> errorMessage = Collections.singletonList(SESSION_EXPIRED_MESSAGE);
                LOGGER.error(errorMessage);
                throw new ValidationException(new CustomError(UNAUTHORIZED_ERROR_CODE, errorMessage));
            }
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_USERNAME, username));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }
}