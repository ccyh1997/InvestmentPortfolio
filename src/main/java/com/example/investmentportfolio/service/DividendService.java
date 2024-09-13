package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.DividendDto;

import java.util.List;

public interface DividendService {
    DividendDto createDividend(DividendDto dividendDto);
    List<DividendDto> getAllDividends();
    DividendDto getDividendById(Long dividendId);
    List<DividendDto> getDividendsByStockId(Long stockId);
    List<DividendDto> getDividendsByExchangeId(Long exchangeId);
    DividendDto updateDividendById(Long dividendId, DividendDto dividendDto);
    void deleteAllDividends();
    void deleteDividendById(Long dividendId);
}
