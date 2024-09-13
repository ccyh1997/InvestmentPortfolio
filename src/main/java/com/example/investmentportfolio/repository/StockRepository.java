package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByStockTickerIgnoreCase(String stockTicker);
    List<Stock> findByStockTypeIgnoreCase(String stockType);
    List<Stock> findByExchangeId(Long exchangeId);
    List<Stock> findByDivInd(String divInd);
    List<Stock> findByDelistInd(String divInd);
    void deleteByStockTickerIgnoreCase(String stockTicker);
    boolean existsByStockTickerIgnoreCase(String stockTicker);
    @Query(value = "SELECT stock_id FROM stocks WHERE stock_ticker = ?1 AND exchange_id = ?2", nativeQuery = true)
    Optional<Long> findIdByTickerAndExchangeId(String stockTicker, Long exchangeId);
    @Query(value = "SELECT exchange from exchanges WHERE exchange_id = (SELECT exchange_id FROM stocks WHERE stock_id = ?1);", nativeQuery = true)
    Optional<String> findExchangeByStockId(Long stockId);
    @Query(value = "SELECT DISTINCT stock_id from transactions WHERE user_id = :userId", nativeQuery = true)
    List<Long> findAllStockIdsByUserId(@Param("userId") Long userId);
    @Query(value = "SELECT * from stocks WHERE stock_id IN (?1) AND div_ind = 'Y';", nativeQuery = true)
    List<Optional<Stock>> findDividendStocksByIds(List<Long> stockIds);
    @Query(value = "SELECT * from stocks WHERE stock_id IN (?1);", nativeQuery = true)
    List<Optional<Stock>> findByStockIds(List<Long> stockIds);
    @Query(value = "SELECT base_currency FROM stocks WHERE stock_id = :stockId", nativeQuery = true)
    String findBaseCurrencyByStockId(@Param("stockId") Long stockId);
    @Query(value = "SELECT stock_ticker FROM stocks WHERE stock_id = :stockId", nativeQuery = true)
    String findStockTickerByStockId(@Param("stockId") Long stockId);
    @Query(value = "SELECT last_price FROM stocks WHERE stock_id = :stockId", nativeQuery = true)
    BigDecimal findLastPriceByStockId(@Param("stockId") Long stockId);
    @Modifying
    @Query(value = "UPDATE stocks SET last_price = ?1 WHERE stock_ticker = ?2 AND exchange_id = ?3", nativeQuery = true)
    void updateLastPriceByStockTickerAndExchange(BigDecimal lastPrice, String stockTicker, Long exchangeId);
}
