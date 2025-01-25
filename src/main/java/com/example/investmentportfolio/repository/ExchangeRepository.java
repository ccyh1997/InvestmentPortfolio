package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Exchange;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findByCountryCodeIgnoreCase(String countryCode);

    Optional<Exchange> findBySuffixIgnoreCase(String suffix);

    @Transactional
    void deleteBySuffixIgnoreCase(String suffix);

    boolean existsByExchangeOrSuffixIgnoreCase(String exchangeName, String suffix);

    @Query(value = "SELECT exchange_id FROM exchanges WHERE exchange = ?1", nativeQuery = true)
    Optional<Long> findIdByExchange(String exchange);

    @Query(value = "SELECT CAST(suffix AS VARCHAR) FROM exchanges WHERE exchange = ?1", nativeQuery = true)
    String findSuffixByExchange(String exchange);
}