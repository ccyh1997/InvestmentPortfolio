package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.StatisticDto;

import java.math.BigDecimal;
import java.util.List;

public interface StatisticService {
    StatisticDto createStatistic(StatisticDto statisticDto);
    List<StatisticDto> getAllStatistics();
    StatisticDto getStatisticById(Long statisticId);
    List<StatisticDto> getStatisticsByUserId(Long userId);
    StatisticDto updateStatisticById(Long statisticId, StatisticDto statisticDto);
    void deleteAllStatistics();
    void deleteStatisticById(Long statisticId);
    void deleteStatisticsByUserId(Long userId);
    BigDecimal calculateTotalUnitsOwnedOnGivenDate(Long userId, Long stockId, String date);
    void calculateTotalUnits(Long userId);
    BigDecimal calculateTotalDividendsEarnedByStock(Long userId, Long stockId);
    void calculateTotalDividendsEarned(Long userId);
    BigDecimal calculateTotalCostByStock(Long userId, Long stockId);
    void calculateTotalCost(Long userId);
    BigDecimal calculateTotalValueByStock(Long userId, Long stockId);
    void calculateTotalValue(Long userId);
    BigDecimal calculateRealizedProfitsByStock(Long userId, Long stockId);
    void calculateRealizedProfits(Long userId);
    BigDecimal calculateUnrealizedProfitsByStock(Long userId, Long stockId);
    void calculateUnrealizedProfits(Long userId);
    BigDecimal calculateTotalProfitsByStock(Long userId, Long stockId);
    void calculateTotalProfits(Long userId);
    void updateStatisticsForUser(Long userId);
    void updateStatisticsForAllUsers();
    void updateTotalProfitsForUser(Long userId);
    void updateTotalProfitsForAllUsers();
}
