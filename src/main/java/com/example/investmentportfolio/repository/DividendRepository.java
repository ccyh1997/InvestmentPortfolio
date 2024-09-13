package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long> {
    boolean existsByExDateOrPayDate(String exDate, String payDate);
    List<Dividend> findByStockId(Long stockId);
    List<Dividend> findByExchangeId(Long exchangeId);
    @Query(value = "SELECT * FROM dividends WHERE stock_id = ?1 AND ex_date >= CAST(?2 AS DATE) ORDER BY ex_date ASC", nativeQuery = true)
    List<Dividend> getRelevantDividends(Long stockId, String date);
}
