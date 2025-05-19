package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.DividendDto;
import com.example.investmentportfolio.model.Dividend;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DividendMapperTest {
    private final DividendMapper dividendMapper = Mappers.getMapper(DividendMapper.class);

    @Test
    void givenDividend_whenConvertToDto_thenReturnDividendDto() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, null, "2023-08-17", "2023-08-30", "10");
        DividendDto dividendDto = dividendMapper.convertToDto(dividend);
        assertEquals("D05", dividendDto.getStockTicker());
        assertNull(dividendDto.getExchange());
        assertEquals("2023-08-17", dividendDto.getExDate());
        assertEquals("2023-08-30", dividendDto.getPayDate());
        assertEquals("10", dividendDto.getPayout());
    }

    @Test
    void givenNullDividend_whenConvertToDto_thenReturnNull() {
        DividendDto dividendDto = dividendMapper.convertToDto(null);
        assertNull(dividendDto);
    }

    @Test
    void givenDividendDto_whenConvertToEntity_thenReturnDividend() {
        DividendDto dividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Dividend dividend = dividendMapper.convertToEntity(dividendDto);
        assertEquals("D05", dividend.getStockTicker());
        assertEquals("SGX", dividend.getExchange());
        assertEquals("2023-08-17", dividend.getExDate());
        assertEquals("2023-08-30", dividend.getPayDate());
        assertEquals("0.0305", dividend.getPayout());
    }

    @Test
    void givenNullDividendDto_whenConvertToEntity_thenReturnNull() {
        Dividend dividend = dividendMapper.convertToEntity(null);
        assertNull(dividend);
    }

    @Test
    void givenDividendDto_whenUpdateEntityWithDto_thenReturnDividend() {
        DividendDto dividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        dividendMapper.updateEntityWithDto(dividendDto, dividend);
        assertEquals("D05", dividend.getStockTicker());
        assertEquals("SGX", dividend.getExchange());
        assertEquals("2023-08-17", dividend.getExDate());
        assertEquals("2023-08-30", dividend.getPayDate());
        assertEquals("0.0305", dividend.getPayout());
    }

    @Test
    void givenNullDividendDto_whenUpdateEntityWithDto_thenReturnNull() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Dividend updatedDividend = dividendMapper.updateEntityWithDto(null, dividend);
        assertEquals(dividend, updatedDividend);
    }

    @Test
    void givenNullDividendDtoFields_whenUpdateEntityWithDto_thenSkipUpdate() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        DividendDto dividendDto = new DividendDto(null, null, null, null, null);
        Dividend updatedDividend = dividendMapper.updateEntityWithDto(dividendDto, dividend);
        assertEquals(dividend, updatedDividend);
    }
}