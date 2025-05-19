package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.StatisticDto;
import com.example.investmentportfolio.model.Statistic;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StatisticMapperTest {
    private final StatisticMapper statisticMapper = Mappers.getMapper(StatisticMapper.class);

    @Test
    void givenStatistic_whenConvertToDto_thenReturnStatisticDto() {
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, null, "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90");
        StatisticDto statisticDto = statisticMapper.convertToDto(statistic);
        assertEquals("testUser", statisticDto.getUsername());
        assertNull(statisticDto.getStockTicker());
        assertEquals("SGX", statisticDto.getExchange());
        assertEquals("2849.99", statisticDto.getTotalUnits());
        assertEquals("96.66", statisticDto.getTotalCost());
        assertEquals("25.74", statisticDto.getTotalValue());
        assertEquals("11.44", statisticDto.getRealizedProfits());
        assertEquals("42.01", statisticDto.getUnrealizedProfits());
        assertEquals("67.65", statisticDto.getDividendsEarned());
        assertEquals("90", statisticDto.getTotalProfits());
    }

    @Test
    void givenNullStatistic_whenConvertToDto_thenReturnNull() {
        StatisticDto statisticDto = statisticMapper.convertToDto(null);
        assertNull(statisticDto);
    }

    @Test
    void givenStatisticDto_whenConvertToEntity_thenReturnStatistic() {
        StatisticDto statisticDto = new StatisticDto("testUser", "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        Statistic statistic = statisticMapper.convertToEntity(statisticDto);
        assertEquals("testUser", statistic.getUsername());
        assertEquals("D05", statistic.getStockTicker());
        assertEquals("SGX", statistic.getExchange());
        assertEquals("2849.99", statistic.getTotalUnits());
        assertEquals("96.66", statistic.getTotalCost());
        assertEquals("25.74", statistic.getTotalValue());
        assertEquals("11.44", statistic.getRealizedProfits());
        assertEquals("42.01", statistic.getUnrealizedProfits());
        assertEquals("67.65", statistic.getDividendsEarned());
        assertEquals("90.65", statistic.getTotalProfits());
    }

    @Test
    void givenNullStatisticDto_whenConvertToEntity_thenReturnNull() {
        Statistic statistic = statisticMapper.convertToEntity(null);
        assertNull(statistic);
    }

    @Test
    void givenStatisticDto_whenUpdateEntityWithDto_thenReturnStatistic() {
        StatisticDto statisticDto = new StatisticDto("testUser", "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        statisticMapper.updateEntityWithDto(statisticDto, statistic);
        assertEquals("testUser", statistic.getUsername());
        assertEquals("D05", statistic.getStockTicker());
        assertEquals("SGX", statistic.getExchange());
        assertEquals("2849.99", statistic.getTotalUnits());
        assertEquals("96.66", statistic.getTotalCost());
        assertEquals("25.74", statistic.getTotalValue());
        assertEquals("11.44", statistic.getRealizedProfits());
        assertEquals("42.01", statistic.getUnrealizedProfits());
        assertEquals("67.65", statistic.getDividendsEarned());
        assertEquals("90.65", statistic.getTotalProfits());
    }

    @Test
    void givenNullStatisticDto_whenUpdateEntityWithDto_thenReturnNull() {
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        Statistic updatedStatistic = statisticMapper.updateEntityWithDto(null, statistic);
        assertEquals(statistic, updatedStatistic);
    }

    @Test
    void givenNullStatisticDtoFields_whenUpdateEntityWithDto_thenSkipUpdate() {
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, null, "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90");
        StatisticDto statisticDto = new StatisticDto(null, null, null, null, null, null, null, null, null, null);
        Statistic updatedStatistic = statisticMapper.updateEntityWithDto(statisticDto, statistic);
        assertEquals(statistic, updatedStatistic);
    }
}