package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.mapper.StockMapper;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.model.Stock;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.repository.StockRepository;
import com.example.investmentportfolio.service.StockService;
import com.example.investmentportfolio.util.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StockServiceImpl implements StockService {
    private static final Logger LOGGER = LogManager.getLogger(StockServiceImpl.class);
    public static final String NO_STOCK_FOUND_WITH_ID = "No stock found with id: %d";
    public static final String NO_STOCK_FOUND_WITH_TICKER = "No stock found with ticker: %s";
    public static final String NO_EXCHANGE_FOUND_WITH_NAME = "No exchange found with name: %s";
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
        Set<ConstraintViolation<StockDto>> violations = validator.validate(stockDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        }
        else {
            Stock stock = stockMapper.convertToEntity(stockDto);
            Optional<Long> optionalId = exchangeRepository.findIdByExchange(stockDto.getExchange().toUpperCase());
            if (optionalId.isPresent()) {
                stock.setExchangeId(optionalId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_NAME, stockDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            if (stockRepository.existsByStockTickerIgnoreCase(stock.getStockTicker())) {
                List<String> errorMessages = Collections.singletonList("A stock with the same ticker already exists.");
                LOGGER.error(errorMessages);
                throw new AlreadyExistsException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
            } else {
                stockRepository.save(stock);
                return stockMapper.convertToDto(stock);
            }
        }
    }

    @Override
    public List<StockDto> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        if (!stocks.isEmpty()) {
            return stocks.stream()
                    .map(stock -> {
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
                        optionalExchange.ifPresent(exchange -> stock.setExchange(String.valueOf(exchange.getExchange())));
                        return stockMapper.convertToDto(stock);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No stock(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public StockDto getStockById(Long stockId) {
        Optional<Stock> optionalStock = stockRepository.findById(stockId);
        if (optionalStock.isPresent()) {
            Stock stock = optionalStock.get();
            Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
            optionalExchange.ifPresent(exchange -> stock.setExchange(String.valueOf(exchange.getExchange())));
            return stockMapper.convertToDto(stock);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STOCK_FOUND_WITH_ID, stockId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public StockDto getStockByTicker(String stockTicker) {
        Optional<Stock> optionalStock = stockRepository.findByStockTickerIgnoreCase(stockTicker);
        if (optionalStock.isPresent()) {
            Stock stock = optionalStock.get();
            Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
            optionalExchange.ifPresent(exchange -> stock.setExchange(String.valueOf(exchange.getExchange())));
            return stockMapper.convertToDto(stock);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STOCK_FOUND_WITH_TICKER, stockTicker));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<StockDto> getStocksByType(String stockType) {
        List<Stock> stocks = stockRepository.findByStockTypeIgnoreCase(stockType);
        if (!stocks.isEmpty()) {
            return stocks.stream()
                    .map(stock -> {
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
                        optionalExchange.ifPresent(exchange -> stock.setExchange(String.valueOf(exchange.getExchange())));
                        return stockMapper.convertToDto(stock);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No stock(s) found with type: %s", stockType));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<StockDto> getStocksByExchangeId(Long exchangeId) {
        List<Stock> stocks = stockRepository.findByExchangeId(exchangeId);
        if (!stocks.isEmpty()) {
            return stocks.stream()
                    .map(stock -> {
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
                        optionalExchange.ifPresent(ex -> stock.setExchange(String.valueOf(ex.getExchange())));
                        return stockMapper.convertToDto(stock);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No stock(s) found from exchange id: %d", exchangeId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<StockDto> getStocksByDividendIndicator(String divInd) {
        List<Stock> stocks = stockRepository.findByDivInd(divInd);
        if (!stocks.isEmpty()) {
            return stocks.stream()
                    .map(stock -> {
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
                        optionalExchange.ifPresent(ex -> stock.setExchange(String.valueOf(ex.getExchange())));
                        return stockMapper.convertToDto(stock);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No stock(s) found with dividend indicator: %s", divInd));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<StockDto> getStocksByDelistIndicator(String delistInd) {
        List<Stock> stocks = stockRepository.findByDelistInd(delistInd);
        if (!stocks.isEmpty()) {
            return stocks.stream()
                    .map(stock -> {
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(stock.getExchangeId());
                        optionalExchange.ifPresent(ex -> stock.setExchange(String.valueOf(ex.getExchange())));
                        return stockMapper.convertToDto(stock);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No stock(s) found with delist indicator: %s", delistInd));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public StockDto updateStockById(Long stockId, StockDto stockDto) {
        Optional<Stock> optionalStock = stockRepository.findById(stockId);
        if (optionalStock.isPresent()) {
            Stock updatedStock = stockMapper.updateEntityWithDto(stockDto, optionalStock.get());
            Optional<Long> optionalId = exchangeRepository.findIdByExchange(stockDto.getExchange().toUpperCase());
            if (optionalId.isPresent()) {
                updatedStock.setExchangeId(optionalId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_NAME, stockDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            stockRepository.save(updatedStock);
            return stockMapper.convertToDto(updatedStock);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STOCK_FOUND_WITH_ID, stockId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public StockDto updateStockByTicker(String stockTicker, StockDto stockDto) {
        Optional<Stock> optionalStock = stockRepository.findByStockTickerIgnoreCase(stockTicker);
        if (optionalStock.isPresent()) {
            Stock updatedStock = stockMapper.updateEntityWithDto(stockDto, optionalStock.get());
            Optional<Long> optionalId = exchangeRepository.findIdByExchange(stockDto.getExchange().toUpperCase());
            if (optionalId.isPresent()) {
                updatedStock.setExchangeId(optionalId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_NAME, stockDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            stockRepository.save(updatedStock);
            return stockMapper.convertToDto(updatedStock);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STOCK_FOUND_WITH_TICKER, stockTicker));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        if (!stocks.isEmpty()) {
            stockRepository.deleteAll();
        } else {
            List<String> errorMessages = Collections.singletonList("No stock(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteStockById(Long stockId) {
        Optional<Stock> optionalStock = stockRepository.findById(stockId);
        if (optionalStock.isPresent()) {
            stockRepository.deleteById(stockId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STOCK_FOUND_WITH_ID, stockId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
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
            List<String> errorMessages = Collections.singletonList(String.format(NO_STOCK_FOUND_WITH_TICKER, stockTicker));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void updateLiveStockPrices() throws IOException, URISyntaxException {
        List<StockDto> stockDtos = getAllStocks();
        if (!stockDtos.isEmpty()) {
            for (StockDto stockDto : stockDtos) {
                String stockTicker = stockDto.getStockTicker();
                String exchange = stockDto.getExchange();
                String suffix = exchangeRepository.findSuffixByExchange(exchange);
                Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(exchange);
                if (optionalExchangeId.isPresent()) {
                    BigDecimal lastPrice = getLastPriceForStock(stockTicker, suffix);
                    stockRepository.updateLastPriceByStockTickerAndExchange(lastPrice, stockTicker, optionalExchangeId.get());
                } else {
                    List<String> errorMessages = Collections.singletonList(String.format("Stock with ticker %s cannot be found in exchange: %s", stockTicker, exchange));
                    LOGGER.error(errorMessages);
                    throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                }
            }
        } else {
            List<String> errorMessages = Collections.singletonList("No stocks found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    private BigDecimal getLastPriceForStock(String stockTicker, String suffix) throws IOException, URISyntaxException {
        String stockTickerAndSuffix = (suffix != null) ? stockTicker + suffix : stockTicker;
        String urlString = "https://query1.finance.yahoo.com/v8/finance/chart/" + stockTickerAndSuffix;
        URI uri = new URI(urlString);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(connection.getInputStream());
        JsonNode resultNode = rootNode.path("chart").path("result").get(0);
        JsonNode metaNode = resultNode.path("meta");
        BigDecimal regularMarketPrice = metaNode.path("regularMarketPrice").decimalValue();
        String currency = metaNode.path("currency").asText();
        connection.disconnect();
        LOGGER.info("{}: {} {}", stockTicker, regularMarketPrice, currency);
        return regularMarketPrice;
    }
}
