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
import com.example.investmentportfolio.util.CreateValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.investmentportfolio.util.Constants.*;

@Service
public class DividendServiceImpl implements DividendService {
    private static final Logger LOGGER = LogManager.getLogger(DividendServiceImpl.class);
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
        validateRequestDto(dividendDto);
        Dividend dividend = dividendMapper.convertToEntity(dividendDto);
        Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(dividendDto.getExchange().toUpperCase());
        if (optionalExchangeId.isPresent()) {
            dividend.setExchangeId(optionalExchangeId.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_NAME, dividendDto.getExchange());
        }
        Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(dividendDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
        if (optionalStockId.isPresent()) {
            dividend.setStockId(optionalStockId.get());
        } else {
            throw returnNotFoundException(LOGGER, STOCK_TICKER_NOT_FOUND_IN_EXCHANGE, dividendDto.getStockTicker(), dividendDto.getExchange());
        }
        if (dividendRepository.existsByExDateOrPayDate(dividend.getExDate(), dividend.getPayDate())) {
            throw returnAlreadyExistsException(LOGGER, DIVIDEND_WITH_SAME_EX_OR_PAY_DATE_EXISTS);
        } else {
            dividendRepository.save(dividend);
            return dividendMapper.convertToDto(dividend);
        }
    }

    @Override
    public List<DividendDto> getAllDividends() {
        List<Dividend> dividends = dividendRepository.findAll();
        if (!dividends.isEmpty()) {
            return dividends.stream().map(dividend -> {
                Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
                if (optionalStock.isPresent()) {
                    dividend.setStockTicker(optionalStock.get().getStockTicker());
                } else {
                    throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, dividend.getStockId());
                }
                Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
                if (optionalExchange.isPresent()) {
                    dividend.setStockTicker(optionalExchange.get().getExchange());
                } else {
                    throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, dividend.getExchangeId());
                }
                return dividendMapper.convertToDto(dividend);
            }).toList();
        } else {
            return List.of();
        }
    }

    @Override
    public DividendDto getDividendById(Long dividendId) {
        Optional<Dividend> optionalDividend = dividendRepository.findById(dividendId);
        if (optionalDividend.isPresent()) {
            Dividend dividend = optionalDividend.get();
            Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
            if (optionalStock.isPresent()) {
                dividend.setStockTicker(optionalStock.get().getStockTicker());
            } else {
                throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, dividend.getStockId());
            }
            Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
            if (optionalExchange.isPresent()) {
                dividend.setStockTicker(optionalExchange.get().getExchange());
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, dividend.getExchangeId());
            }
            return dividendMapper.convertToDto(dividend);
        } else {
            throw returnNotFoundException(LOGGER, NO_DIVIDEND_FOUND_WITH_ID, dividendId);
        }
    }

    @Override
    public List<DividendDto> getDividendsByStockId(Long stockId) {
        List<Dividend> dividends = dividendRepository.findByStockId(stockId);
        if (!dividends.isEmpty()) {
            return dividends.stream().map(dividend -> {
                Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
                if (optionalStock.isPresent()) {
                    dividend.setStockTicker(optionalStock.get().getStockTicker());
                } else {
                    throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, dividend.getStockId());
                }
                Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
                if (optionalExchange.isPresent()) {
                    dividend.setStockTicker(optionalExchange.get().getExchange());
                } else {
                    throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, dividend.getExchangeId());
                }
                return dividendMapper.convertToDto(dividend);
            }).toList();
        } else {
            return List.of();
        }
    }

    @Override
    public List<DividendDto> getDividendsByExchangeId(Long exchangeId) {
        List<Dividend> dividends = dividendRepository.findByExchangeId(exchangeId);
        if (!dividends.isEmpty()) {
            return dividends.stream().map(dividend -> {
                Optional<Stock> optionalStock = stockRepository.findById(dividend.getStockId());
                if (optionalStock.isPresent()) {
                    dividend.setStockTicker(optionalStock.get().getStockTicker());
                } else {
                    throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, dividend.getStockId());
                }
                Optional<Exchange> optionalExchange = exchangeRepository.findById(dividend.getExchangeId());
                if (optionalExchange.isPresent()) {
                    dividend.setStockTicker(optionalExchange.get().getExchange());
                } else {
                    throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, dividend.getExchangeId());
                }
                return dividendMapper.convertToDto(dividend);
            }).toList();
        } else {
            return List.of();
        }
    }

    @Override
    public DividendDto updateDividendById(Long dividendId, DividendDto dividendDto) {
        validateRequestDto(dividendDto);
        Optional<Dividend> optionalDividend = dividendRepository.findById(dividendId);
        if (optionalDividend.isPresent()) {
            Dividend updatedDividend = dividendMapper.updateEntityWithDto(dividendDto, optionalDividend.get());
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(dividendDto.getExchange().toUpperCase());
            if (optionalExchangeId.isPresent()) {
                updatedDividend.setExchangeId(optionalExchangeId.get());
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_NAME, dividendDto.getExchange());
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(dividendDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                updatedDividend.setStockId(optionalStockId.get());
            } else {
                throw returnNotFoundException(LOGGER, STOCK_TICKER_NOT_FOUND_IN_EXCHANGE, dividendDto.getStockTicker(), dividendDto.getExchange());
            }
            dividendRepository.save(updatedDividend);
            return dividendMapper.convertToDto(updatedDividend);
        } else {
            throw returnNotFoundException(LOGGER, NO_DIVIDEND_FOUND_WITH_ID, dividendId);
        }
    }

    @Override
    @Transactional
    public void deleteAllDividends() {
        List<Dividend> dividends = dividendRepository.findAll();
        if (!dividends.isEmpty()) {
            dividendRepository.deleteAll();
        } else {
            throw returnNotFoundException(LOGGER, NO_DIVIDENDS_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteDividendById(Long dividendId) {
        Optional<Dividend> optionalDividend = dividendRepository.findById(dividendId);
        if (optionalDividend.isPresent()) {
            dividendRepository.deleteById(dividendId);
        } else {
            throw returnNotFoundException(LOGGER, NO_DIVIDEND_FOUND_WITH_ID, dividendId);
        }
    }

    private void validateRequestDto(DividendDto dividendDto) {
        Set<ConstraintViolation<DividendDto>> violations = validator.validate(dividendDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            throw returnValidationException(LOGGER, violations);
        }
    }
}