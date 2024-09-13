package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findByRateNameIgnoreCase(String rate);
    boolean existsByRateNameIgnoreCase(String rateName);
}
