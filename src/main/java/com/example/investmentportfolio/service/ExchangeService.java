package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.ExchangeDto;

import java.util.List;

public interface ExchangeService {
    ExchangeDto createExchange(ExchangeDto exchangeDto);
    List<ExchangeDto> getAllExchanges();
    ExchangeDto getExchangeById(Long exchangeId);
    List<ExchangeDto> getExchangesByCountryCode(String countryCode);
    ExchangeDto getExchangeBySuffix(String suffix);
    ExchangeDto updateExchangeById(Long exchangeId, ExchangeDto exchangeDto);
    ExchangeDto updateExchangeBySuffix(String suffix, ExchangeDto exchangeDto);
    void deleteAllExchanges();
    void deleteExchangeById(Long exchangeId);
    ExchangeDto deleteExchangeBySuffix(String suffix);
}