package com.example.investmentportfolio.security;

import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.util.Constants;
import com.example.investmentportfolio.util.CustomError;
import com.example.investmentportfolio.util.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.investmentportfolio.util.Constants.*;

@Component
public class RefreshTokenProvider {
    private static final Logger LOGGER = LogManager.getLogger(RefreshTokenProvider.class);
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public RefreshTokenProvider(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public UUID generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Optional<Long> optionalUserId = userRepository.findIdByUsername(username.toUpperCase());
        if (optionalUserId.isPresent()) {
            Long userId = optionalUserId.get();
            Optional<UUID> optionalValidRefreshToken = Optional.ofNullable(tokenRepository.getValidRefreshToken(userId));
            if (optionalValidRefreshToken.isPresent()) {
                LOGGER.info(VALID_REFRESH_TOKEN_MESSAGE);
                return optionalValidRefreshToken.get();
            } else {
                tokenRepository.deleteByUserId(userId);
                UUID newRefreshToken;
                do {
                    newRefreshToken = UUID.randomUUID();
                } while (tokenRepository.existsByRefreshToken(newRefreshToken));
                tokenRepository.saveRefreshToken(userId, newRefreshToken);
                LOGGER.info(GENERATE_REFRESH_TOKEN_MESSAGE);
                return newRefreshToken;
            }
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_USERNAME, username));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }
}
