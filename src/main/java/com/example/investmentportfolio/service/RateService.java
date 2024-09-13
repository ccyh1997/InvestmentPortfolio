package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.RateDto;

import java.util.List;

public interface RateService {
    RateDto createRate(RateDto rateDto);
    List<RateDto> getAllRates();
    RateDto getRateById(Long rateId);
    RateDto updateRateById(Long rateId, RateDto rateDto);
    void deleteAllRates();
    void deleteRateById(Long rateId);
}
