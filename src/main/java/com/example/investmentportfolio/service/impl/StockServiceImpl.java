package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.mapper.StockMapper;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.model.Stock;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.repository.StockRepository;
import com.example.investmentportfolio.service.StockService;
import com.example.investmentportfolio.util.CreateValidation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.investmentportfolio.util.Constants.*;

@Service
public class StockServiceImpl implements StockService {
    private static final Logger LOGGER = LogManager.getLogger(StockServiceImpl.class);
    private final StockRepository stockRepository;
    private final ExchangeRepository exchangeRepository;
    private final StockMapper stockMapper;
    private final Validator validator;

    public StockServiceImpl(StockRepository stockRepository, ExchangeRepository exchangeRepository, StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.exchangeRepository = exchangeRepository;
        this.stockMapper = stockMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public StockDto createStock(StockDto stockDto) {
        validateRequestDto(stockDto);
        Stock stock = stockMapper.convertToEntity(stockDto);
        Optional<Long> optionalId = exchangeRepository.findIdByExchange(stockDto.getExchange().toUpperCase());
        if (optionalId.isPresent()) {
            stock.setExchangeId(optionalId.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_NAME, stockDto.getExchange());
        }
        if (stockRepository.existsByStockTickerIgnoreCase(stock.getStockTicker())) {
            throw returnAlreadyExistsException(LOGGER, STOCK_WITH_SAME_TICKER_ALREADY_EXISTS);
        } else {
            stockRepository.save(stock);
            return stockMapper.convertToDto(stock);
        }
    }

    @Override
    public List<StockDto> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        if (!stocks.isEmpty()) {
            return stocks.stream().map(stock -> {
                Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
                if (optionalExchange.isPresent()) {
                    stock.setStockTicker(optionalExchange.get().getExchange());
                } else {
                    throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, stock.getExchangeId());
                }
                return stockMapper.convertToDto(stock);
            }).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCKS_FOUND);
        }
    }

    @Override
    public StockDto getStockById(Long stockId) {
        Optional<Stock> optionalStock = stockRepository.findById(stockId);
        if (optionalStock.isPresent()) {
            Stock stock = optionalStock.get();
            Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
            if (optionalExchange.isPresent()) {
                stock.setStockTicker(optionalExchange.get().getExchange());
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, stock.getExchangeId());
            }
            return stockMapper.convertToDto(stock);
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, stockId);
        }
    }

    @Override
    public StockDto getStockByTicker(String stockTicker) {
        Optional<Stock> optionalStock = stockRepository.findByStockTickerIgnoreCase(stockTicker);
        if (optionalStock.isPresent()) {
            Stock stock = optionalStock.get();
            Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
            if (optionalExchange.isPresent()) {
                stock.setStockTicker(optionalExchange.get().getExchange());
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, stock.getExchangeId());
            }
            return stockMapper.convertToDto(stock);
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_TICKER, stockTicker);
        }
    }

    @Override
    public List<StockDto> getStocksByFilters(Long exchangeId, String stockType, String divInd, String delistInd) {
        List<Stock> stocks = stockRepository.findByFilters(exchangeId, stockType, divInd, delistInd);
        if (!stocks.isEmpty()) {
            return stocks.stream().map(stock -> {
                Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
                if (optionalExchange.isPresent()) {
                    stock.setStockTicker(optionalExchange.get().getExchange());
                } else {
                    throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, stock.getExchangeId());
                }
                return stockMapper.convertToDto(stock);
            }).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCKS_FOUND_WITH_FILTERS);
        }
    }

    @Override
    public StockDto updateStockById(Long stockId, StockDto stockDto) {
        validateRequestDto(stockDto);
        Optional<Stock> optionalStock = stockRepository.findById(stockId);
        if (optionalStock.isPresent()) {
            Stock updatedStock = stockMapper.updateEntityWithDto(stockDto, optionalStock.get());
            Optional<Long> optionalId = exchangeRepository.findIdByExchange(stockDto.getExchange().toUpperCase());
            if (optionalId.isPresent()) {
                updatedStock.setExchangeId(optionalId.get());
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_NAME, stockDto.getExchange());
            }
            stockRepository.save(updatedStock);
            return stockMapper.convertToDto(updatedStock);
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, stockId);
        }
    }

    @Override
    public StockDto updateStockByTicker(String stockTicker, StockDto stockDto) {
        validateRequestDto(stockDto);
        Optional<Stock> optionalStock = stockRepository.findByStockTickerIgnoreCase(stockTicker);
        if (optionalStock.isPresent()) {
            Stock updatedStock = stockMapper.updateEntityWithDto(stockDto, optionalStock.get());
            Optional<Long> optionalId = exchangeRepository.findIdByExchange(stockDto.getExchange().toUpperCase());
            if (optionalId.isPresent()) {
                updatedStock.setExchangeId(optionalId.get());
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_NAME, stockDto.getExchange());
            }
            stockRepository.save(updatedStock);
            return stockMapper.convertToDto(updatedStock);
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_TICKER, stockTicker);
        }
    }

    @Override
    @Transactional
    public void deleteAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        if (!stocks.isEmpty()) {
            stockRepository.deleteAll();
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCKS_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteStockById(Long stockId) {
        Optional<Stock> optionalStock = stockRepository.findById(stockId);
        if (optionalStock.isPresent()) {
            stockRepository.deleteById(stockId);
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, stockId);
        }
    }

    @Override
    @Transactional
    public StockDto deleteStockByTicker(String stockTicker) {
        Optional<Stock> optionalStock = stockRepository.findByStockTickerIgnoreCase(stockTicker);
        if (optionalStock.isPresent()) {
            stockRepository.deleteByStockTickerIgnoreCase(stockTicker);
            return stockMapper.convertToDto(optionalStock.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_TICKER, stockTicker);
        }
    }

    @Override
    @Transactional
    public void updateLiveStockPrices() throws IOException, URISyntaxException {
        List<StockDto> stockDtos = getAllStocks();
        for (StockDto stockDto : stockDtos) {
            String stockTicker = stockDto.getStockTicker();
            String exchange = stockDto.getExchange();
            String suffix = exchangeRepository.findSuffixByExchange(exchange);
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(exchange);
            if (optionalExchangeId.isPresent()) {
                BigDecimal lastPrice = getLastPriceForStock(stockTicker, suffix);
                stockRepository.updateLastPriceByStockTickerAndExchange(lastPrice, stockTicker, optionalExchangeId.get());
            } else {
                throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_IN_EXCHANGE, stockTicker, exchange);
            }
        }
    }

    private BigDecimal getLastPriceForStock(String stockTicker, String suffix) throws IOException, URISyntaxException {
        String stockTickerAndSuffix = (suffix != null) ? stockTicker + suffix : stockTicker;
        String urlString = YAHOO_FINANCE_URL + stockTickerAndSuffix;
        URI uri = new URI(urlString);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(GET);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(connection.getInputStream());
        JsonNode resultNode = rootNode.path(CHART).path(RESULT).get(0);
        JsonNode metaNode = resultNode.path(META);
        BigDecimal lastPrice = metaNode.path(REGULAR_MARKET_PRICE).decimalValue();
        String currency = metaNode.path(CURRENCY).asText();
        connection.disconnect();
        LOGGER.info(THREE_PLACEHOLDERS_LOG, stockTicker, lastPrice, currency);
        return lastPrice;
    }

    private void validateRequestDto(StockDto stockDto) {
        Set<ConstraintViolation<StockDto>> violations = validator.validate(stockDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            throw returnValidationException(LOGGER, violations);
        }
    }
}
