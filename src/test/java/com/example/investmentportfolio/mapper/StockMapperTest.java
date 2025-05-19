package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.model.Stock;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StockMapperTest {
    private final StockMapper stockMapper = Mappers.getMapper(StockMapper.class);

    @Test
    void givenStock_whenConvertToDto_thenReturnStockDto() {
        Stock stock = new Stock(1L, null, null, "Equity", 1L, "SGX", "43", "SGD", "Y", "N");
        StockDto stockDto = stockMapper.convertToDto(stock);
        assertNull(stockDto.getStockTicker());
        assertNull(stockDto.getStockName());
        assertEquals("Equity", stockDto.getStockType());
        assertEquals("SGX", stockDto.getExchange());
        assertEquals("43", stockDto.getLastPrice());
        assertEquals("SGD", stockDto.getBaseCurrency());
        assertEquals("Y", stockDto.getDivInd());
        assertEquals("N", stockDto.getDelistInd());
    }

    @Test
    void givenNullStock_whenConvertToDto_thenReturnNull() {
        StockDto stockDto = stockMapper.convertToDto(null);
        assertNull(stockDto);
    }

    @Test
    void givenStockDto_whenConvertToEntity_thenReturnStock() {
        StockDto stockDto = new StockDto("D05", "", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = stockMapper.convertToEntity(stockDto);
        assertEquals("D05", stock.getStockTicker());
        assertEquals("", stock.getStockName());
        assertEquals("Equity", stock.getStockType());
        assertEquals("SGX", stock.getExchange());
        assertEquals("43.62", stock.getLastPrice());
        assertEquals("SGD", stock.getBaseCurrency());
        assertEquals("Y", stock.getDivInd());
        assertEquals("N", stock.getDelistInd());
    }

    @Test
    void givenNullStockDto_whenConvertToEntity_thenReturnNull() {
        Stock stock = stockMapper.convertToEntity(null);
        assertNull(stock);
    }

    @Test
    void givenStockDto_whenUpdateEntityWithDto_thenReturnStock() {
        StockDto stockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        stockMapper.updateEntityWithDto(stockDto, stock);
        assertEquals("D05", stock.getStockTicker());
        assertEquals("DBS Group Holdings Ltd", stock.getStockName());
        assertEquals("Equity", stock.getStockType());
        assertEquals("SGX", stock.getExchange());
        assertEquals("43.62", stock.getLastPrice());
        assertEquals("SGD", stock.getBaseCurrency());
        assertEquals("Y", stock.getDivInd());
        assertEquals("N", stock.getDelistInd());
    }

    @Test
    void givenNullStockDto_whenUpdateEntityWithDto_thenReturnNull() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        Stock updatedStock = stockMapper.updateEntityWithDto(null, stock);
        assertEquals(stock, updatedStock);
    }

    @Test
    void givenNullStockDtoFields_whenUpdateEntityWithDto_thenSkipUpdate() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        StockDto stockDto = new StockDto(null, null, null, null, null, null, null, null);
        Stock updatedStock = stockMapper.updateEntityWithDto(stockDto, stock);
        assertEquals(stock, updatedStock);
    }
}