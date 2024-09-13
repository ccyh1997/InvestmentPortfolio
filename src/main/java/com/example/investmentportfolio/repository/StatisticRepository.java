package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    List<Statistic> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    boolean existsByUserIdAndStockId(Long userId, Long stockId);
    @Modifying
    @Query(value = "UPDATE statistics SET total_units = ?1 WHERE user_id = ?2 AND stock_id = ?3", nativeQuery = true)
    void updateUnits(BigDecimal stockUnits, Long userId, Long stockId);
    @Modifying
    @Query(value = "UPDATE statistics SET total_cost = ?1 WHERE user_id = ?2 AND stock_id = ?3", nativeQuery = true)
    void updateCost(BigDecimal stockCost, Long userId, Long stockId);
    @Query(value = "SELECT total_cost FROM statistics WHERE user_id = :userId AND stock_id = :stockId", nativeQuery = true)
    BigDecimal getCost(@Param("userId") Long userId, @Param("stockId") Long stockId);
    @Modifying
    @Query(value = "UPDATE statistics SET dividends_earned = ?1 WHERE user_id = ?2 AND stock_id = ?3", nativeQuery = true)
    void updateDividends(BigDecimal dividendsEarned, Long userId, Long stockId);
    @Query(value = "SELECT COALESCE(SUM(CAST(dividends_earned AS DECIMAL)), 0) FROM statistics WHERE user_id = ?1 AND stock_id = ?2", nativeQuery = true)
    BigDecimal getDividends(Long userId, Long stockId);
    @Query(value = "SELECT total_units FROM statistics WHERE user_id = ?1 AND stock_id = ?2", nativeQuery = true)
    BigDecimal getStockUnits(Long userId, Long stockId);
    @Modifying
    @Query(value = "UPDATE statistics SET total_value = ?1 WHERE user_id = ?2 AND stock_id = ?3", nativeQuery = true)
    void updateValue(BigDecimal stockValue, Long userId, Long stockId);
    @Query(value = "SELECT total_value FROM statistics WHERE user_id = :userId AND stock_id = :stockId", nativeQuery = true)
    BigDecimal getValue(@Param("userId") Long userId, @Param("stockId") Long stockId);
    @Modifying
    @Query(value = "UPDATE statistics SET unrealized_profits = ?1 WHERE user_id = ?2 AND stock_id = ?3", nativeQuery = true)
    void updateUnrealizedProfits(BigDecimal unrealizedProfits, Long userId, Long stockId);
    @Query(value = "SELECT unrealized_profits FROM statistics WHERE user_id = :userId AND stock_id = :stockId", nativeQuery = true)
    BigDecimal getUnrealizedProfits(@Param("userId") Long userId, @Param("stockId") Long stockId);
    @Modifying
    @Query(value = "UPDATE statistics SET realized_profits = ?1 WHERE user_id = ?2 AND stock_id = ?3", nativeQuery = true)
    void updateRealizedProfits(BigDecimal unrealizedProfits, Long userId, Long stockId);
    @Query(value = "SELECT realized_profits FROM statistics WHERE user_id = :userId AND stock_id = :stockId", nativeQuery = true)
    BigDecimal getRealizedProfits(@Param("userId") Long userId, @Param("stockId") Long stockId);
    @Query(value = "SELECT * FROM statistics WHERE user_id = :userId AND stock_id = :stockId", nativeQuery = true)
    Optional<Statistic> findByUserIdAndStockId(Long userId, Long stockId);
    @Modifying
    @Query(value = "UPDATE statistics SET total_profits = ?1 WHERE user_id = ?2 AND stock_id = ?3", nativeQuery = true)
    void updateTotalProfits(BigDecimal unrealizedProfits, Long userId, Long stockId);
    @Query(value = "SELECT total_profits FROM statistics WHERE user_id = :userId AND stock_id = :stockId", nativeQuery = true)
    BigDecimal getTotalProfits(@Param("userId") Long userId, @Param("stockId") Long stockId);
}
