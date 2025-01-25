package com.example.investmentportfolio.security;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Hidden
@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query(value = "SELECT refresh_token FROM tokens where user_id = ?1 AND NOW() < expiration_time", nativeQuery = true)
    UUID getValidRefreshToken(Long userId);

    boolean existsByRefreshToken(UUID refreshToken);

    @Transactional // @Modifying is not required as it is not a custom query
    void deleteByUserId(Long userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO tokens (user_id, refresh_token, expiration_time) VALUES (?1, ?2, NOW() + INTERVAL '1 day')", nativeQuery = true)
    void saveRefreshToken(Long userId, UUID refreshToken);

    @Query(value = "SELECT COUNT(*) refresh_tokens FROM tokens WHERE user_id = ?1 AND refresh_token = ?2 AND NOW() < expiration_time", nativeQuery = true)
    Integer countValidRefreshTokens(Long userId, UUID refreshToken);
}
