package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.StockDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface StockService {
    StockDto createStock(StockDto stockDto);
    List<StockDto> getAllStocks();
    StockDto getStockById(Long stockId);
    StockDto getStockByTicker(String stockTicker);
    List<StockDto> getStocksByType(String stockType);
    List<StockDto> getStocksByExchangeId(Long exchangeId);
    List<StockDto> getStocksByDividendIndicator(String divInd);
    List<StockDto> getStocksByDelistIndicator(String delistInd);
    StockDto updateStockById(Long stockId, StockDto stockDto);
    StockDto updateStockByTicker(String stockTicker, StockDto stockDto);
    void deleteAllStocks();
    void deleteStockById(Long stockId);
    StockDto deleteStockByTicker(String stockTicker);
    void updateLiveStockPrices() throws IOException, URISyntaxException;
}
