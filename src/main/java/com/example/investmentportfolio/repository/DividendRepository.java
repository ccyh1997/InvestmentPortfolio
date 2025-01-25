package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Dividend;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Hidden
@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long> {
    boolean existsByExDateOrPayDate(String exDate, String payDate);

    List<Dividend> findByStockId(Long stockId);

    List<Dividend> findByExchangeId(Long exchangeId);

    @Query(value = "SELECT * FROM dividends WHERE stock_id = ?1 AND CAST(ex_date AS DATE) >= CAST(?2 AS DATE) ORDER BY ex_date ASC", nativeQuery = true)
    List<Dividend> getRelevantDividends(Long stockId, String date);
}
