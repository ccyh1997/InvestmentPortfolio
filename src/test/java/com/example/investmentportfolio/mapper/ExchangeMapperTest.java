package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.model.Exchange;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExchangeMapperTest {
    private final ExchangeMapper exchangeMapper = Mappers.getMapper(ExchangeMapper.class);

    @Test
    void givenExchange_whenConvertToDto_thenReturnExchangeDto() {
        Exchange exchange = new Exchange(1L, "SGX", null, ".SI");
        ExchangeDto exchangeDto = exchangeMapper.convertToDto(exchange);
        assertEquals("SGX", exchangeDto.getExchange());
        assertNull(exchangeDto.getCountryCode());
        assertEquals(".SI", exchangeDto.getSuffix());
    }

    @Test
    void givenNullExchange_whenConvertToDto_thenReturnNull() {
        ExchangeDto exchangeDto = exchangeMapper.convertToDto(null);
        assertNull(exchangeDto);
    }

    @Test
    void givenExchangeDto_whenConvertToEntity_thenReturnExchange() {
        ExchangeDto exchangeDto = new ExchangeDto("SGX", "SG", ".SI");
        Exchange exchange = exchangeMapper.convertToEntity(exchangeDto);
        assertEquals("SGX", exchange.getExchange());
        assertEquals("SG", exchange.getCountryCode());
        assertEquals(".SI", exchange.getSuffix());
    }

    @Test
    void givenNullExchangeDto_whenConvertToEntity_thenReturnNull() {
        Exchange exchange = exchangeMapper.convertToEntity(null);
        assertNull(exchange);
    }

    @Test
    void givenExchangeDto_whenUpdateEntityWithDto_thenReturnExchange() {
        ExchangeDto exchangeDto = new ExchangeDto("SGX", "SG", ".SI");
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        exchangeMapper.updateEntityWithDto(exchangeDto, exchange);
        assertEquals("SGX", exchange.getExchange());
        assertEquals("SG", exchange.getCountryCode());
        assertEquals(".SI", exchange.getSuffix());
    }

    @Test
    void givenNullExchangeDto_whenUpdateEntityWithDto_thenReturnNull() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        Exchange updatedExchange = exchangeMapper.updateEntityWithDto(null, exchange);
        assertEquals(exchange, updatedExchange);
    }

    @Test
    void givenNullExchangeDtoFields_whenUpdateEntityWithDto_thenSkipUpdate() {
        ExchangeDto exchangeDto = new ExchangeDto(null, null, null);
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        Exchange updatedExchange = exchangeMapper.updateEntityWithDto(exchangeDto, exchange);
        assertEquals(exchange, updatedExchange);
    }
}