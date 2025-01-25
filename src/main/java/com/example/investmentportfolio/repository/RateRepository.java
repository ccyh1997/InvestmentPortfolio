package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Rate;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Hidden
@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findByRateNameIgnoreCase(String rate);

    boolean existsByRateNameIgnoreCase(String rateName);
}
