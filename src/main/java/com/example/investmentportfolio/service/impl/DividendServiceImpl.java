package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.DividendDto;
import com.example.investmentportfolio.mapper.DividendMapper;
import com.example.investmentportfolio.model.Dividend;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.model.Stock;
import com.example.investmentportfolio.repository.DividendRepository;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.repository.StockRepository;
import com.example.investmentportfolio.service.DividendService;
import com.example.investmentportfolio.util.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DividendServiceImpl implements DividendService {
    private static final Logger LOGGER = LogManager.getLogger(DividendServiceImpl.class);
    public static final String NO_DIVIDEND_FOUND_WITH_ID = "No dividend found with id: %d";
    private final DividendRepository dividendRepository;
    private final StockRepository stockRepository;
    private final ExchangeRepository exchangeRepository;
    private final DividendMapper dividendMapper;
    private final Validator validator;

    public DividendServiceImpl(DividendRepository dividendRepository, StockRepository stockRepository, ExchangeRepository exchangeRepository, DividendMapper dividendMapper) {
        this.dividendRepository = dividendRepository;
        this.stockRepository = stockRepository;
        this.exchangeRepository = exchangeRepository;
        this.dividendMapper = dividendMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public DividendDto createDividend(DividendDto dividendDto) {
        Set<ConstraintViolation<DividendDto>> violations = validator.validate(dividendDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                            .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        }
        else {
            Dividend dividend = dividendMapper.convertToEntity(dividendDto);
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(dividendDto.getExchange().toUpperCase());
            if (optionalExchangeId.isPresent()) {
                dividend.setExchangeId(optionalExchangeId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", dividendDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(dividendDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                dividend.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", dividendDto.getStockTicker(), dividendDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            if (dividendRepository.existsByExDateOrPayDate(dividend.getExDate(), dividend.getPayDate())) {
                List<String> errorMessages = Collections.singletonList("An dividend with the same ex date or pay date already exists.");
                LOGGER.error(errorMessages);
                throw new AlreadyExistsException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
            } else {
                dividendRepository.save(dividend);
                return dividendMapper.convertToDto(dividend);
            }
        }
    }

    @Override
    public List<DividendDto> getAllDividends() {
        List<Dividend> dividends = dividendRepository.findAll();
        if (!dividends.isEmpty()) {
            return dividends.stream()
                    .map(dividend -> {
                        Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
                        optionalStock.ifPresent(stock -> dividend.setStockTicker(stock.getStockTicker()));
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
                        optionalExchange.ifPresent(exchange -> dividend.setExchange(exchange.getExchange()));
                        return dividendMapper.convertToDto(dividend);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No dividend(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public DividendDto getDividendById(Long dividendId) {
        Optional<Dividend> optionalDividend = dividendRepository.findById(dividendId);
        if (optionalDividend.isPresent()) {
            Dividend dividend = optionalDividend.get();
            Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
            optionalStock.ifPresent(stock -> dividend.setStockTicker(stock.getStockTicker()));
            Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
            optionalExchange.ifPresent(exchange -> dividend.setExchange(String.valueOf(exchange.getExchange())));
            return dividendMapper.convertToDto(dividend);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_DIVIDEND_FOUND_WITH_ID, dividendId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<DividendDto> getDividendsByStockId(Long stockId) {
        List<Dividend> dividends = dividendRepository.findByStockId(stockId);
        if (!dividends.isEmpty()) {
            return dividends.stream()
                    .map(dividend -> {
                        Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
                        optionalStock.ifPresent(stock -> dividend.setStockTicker(stock.getStockTicker()));
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
                        optionalExchange.ifPresent(exchange -> dividend.setExchange(exchange.getExchange()));
                        return dividendMapper.convertToDto(dividend);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No dividend(s) found with stock id: %d", stockId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<DividendDto> getDividendsByExchangeId(Long exchangeId) {
        List<Dividend> dividends = dividendRepository.findByExchangeId(exchangeId);
        if (!dividends.isEmpty()) {
            return dividends.stream()
                    .map(dividend -> {
                        Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
                        optionalStock.ifPresent(stock -> dividend.setStockTicker(stock.getStockTicker()));
                        Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
                        optionalExchange.ifPresent(exchange -> dividend.setExchange(exchange.getExchange()));
                        return dividendMapper.convertToDto(dividend);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No dividend(s) found with exchange id: %d", exchangeId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public DividendDto updateDividendById(Long dividendId, DividendDto dividendDto) {
        Optional<Dividend> optionalDividend = dividendRepository.findById(dividendId);
        if (optionalDividend.isPresent()) {
            Dividend updatedDividend = dividendMapper.updateEntityWithDto(dividendDto, optionalDividend.get());
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(dividendDto.getExchange().toUpperCase());
            if (optionalExchangeId.isPresent()) {
                updatedDividend.setExchangeId(optionalExchangeId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", dividendDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(dividendDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                updatedDividend.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", dividendDto.getStockTicker(), dividendDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            dividendRepository.save(updatedDividend);
            return dividendMapper.convertToDto(updatedDividend);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_DIVIDEND_FOUND_WITH_ID, dividendId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteAllDividends() {
        List<Dividend> dividends = dividendRepository.findAll();
        if (!dividends.isEmpty()) {
            dividendRepository.deleteAll();
        } else {
            List<String> errorMessages = Collections.singletonList("No dividend(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteDividendById(Long dividendId) {
        Optional<Dividend> optionalDividend = dividendRepository.findById(dividendId);
        if (optionalDividend.isPresent()) {
            dividendRepository.deleteById(dividendId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_DIVIDEND_FOUND_WITH_ID, dividendId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }
}