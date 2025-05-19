package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.model.Rate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RateMapperTest {
    private final RateMapper rateMapper = Mappers.getMapper(RateMapper.class);

    @Test
    void givenRate_whenConvertToDto_thenReturnRateDto() {
        Rate rate = new Rate(1L, null, "2");
        RateDto rateDto = rateMapper.convertToDto(rate);
        assertNull(rateDto.getRateName());
        assertEquals("2", rateDto.getRate());
    }

    @Test
    void givenNullRate_whenConvertToDto_thenReturnNull() {
        RateDto rateDto = rateMapper.convertToDto(null);
        assertNull(rateDto);
    }

    @Test
    void givenRateDto_whenConvertToEntity_thenReturnRate() {
        RateDto rateDto = new RateDto("SGD/USD", "0.74120251");
        Rate rate = rateMapper.convertToEntity(rateDto);
        assertEquals("SGD/USD", rate.getRateName());
        assertEquals("0.74120251", rate.getRate());
    }

    @Test
    void givenNullRateDto_whenConvertToEntity_thenReturnNull() {
        Rate rate = rateMapper.convertToEntity(null);
        assertNull(rate);
    }

    @Test
    void givenRateDto_whenUpdateEntityWithDto_thenReturnRate() {
        RateDto rateDto = new RateDto("SGD/USD", "0.74120251");
        Rate rate = new Rate(1L, "SGD/USD", "0.74120251");
        rateMapper.updateEntityWithDto(rateDto, rate);
        assertEquals("SGD/USD", rate.getRateName());
        assertEquals("0.74120251", rate.getRate());
    }

    @Test
    void givenNullRateDto_whenUpdateEntityWithDto_thenReturnNull() {
        Rate rate = new Rate(1L, "SGD/USD", "0.74120251");
        Rate updatedRate = rateMapper.updateEntityWithDto(null, rate);
        assertEquals(rate, updatedRate);
    }

    @Test
    void givenNullRateDtoFields_whenUpdateEntityWithDto_thenSkipUpdate() {
        Rate rate = new Rate(1L, "SGD/USD", "0.74120251");
        RateDto rateDto = new RateDto(null, null);
        Rate updatedRate = rateMapper.updateEntityWithDto(rateDto, rate);
        assertEquals(rate, updatedRate);
    }
}