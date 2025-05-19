package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.mapper.ExchangeMapper;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.service.ExchangeService;
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
public class ExchangeServiceImpl implements ExchangeService {
    private static final Logger LOGGER = LogManager.getLogger(ExchangeServiceImpl.class);
    private final ExchangeRepository exchangeRepository;
    private final ExchangeMapper exchangeMapper;
    private final Validator validator;

    public ExchangeServiceImpl(ExchangeRepository exchangeRepository, ExchangeMapper exchangeMapper) {
        this.exchangeRepository = exchangeRepository;
        this.exchangeMapper = exchangeMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public ExchangeDto createExchange(ExchangeDto exchangeDto) {
        validateRequestDto(exchangeDto);
        Exchange exchange = exchangeMapper.convertToEntity(exchangeDto);
        if (exchangeRepository.existsByExchangeOrSuffixIgnoreCase(exchange.getExchange(), exchange.getSuffix())) {
            throw returnAlreadyExistsException(LOGGER, EXCHANGE_SAME_NAME_OR_SUFFIX);
        } else {
            exchangeRepository.save(exchange);
            return exchangeMapper.convertToDto(exchange);
        }
    }

    @Override
    public List<ExchangeDto> getAllExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        if (!exchanges.isEmpty()) {
            return exchanges.stream().map(exchangeMapper::convertToDto).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGES_FOUND);
        }
    }

    @Override
    public ExchangeDto getExchangeById(Long exchangeId) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(exchangeId);
        if (optionalExchange.isPresent()) {
            return exchangeMapper.convertToDto(optionalExchange.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, exchangeId);
        }
    }

    @Override
    public List<ExchangeDto> getExchangesByCountryCode(String countryCode) {
        List<Exchange> exchanges = exchangeRepository.findByCountryCodeIgnoreCase(countryCode);
        if (!exchanges.isEmpty()) {
            return exchanges.stream().map(exchangeMapper::convertToDto).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGES_FOUND_WITH_COUNTRY_CODE, countryCode);
        }
    }

    @Override
    public ExchangeDto getExchangeBySuffix(String suffix) {
        Optional<Exchange> optionalExchange = exchangeRepository.findBySuffixIgnoreCase(suffix);
        if (optionalExchange.isPresent()) {
            return exchangeMapper.convertToDto(optionalExchange.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_SUFFIX, suffix);
        }
    }

    @Override
    public ExchangeDto updateExchangeById(Long exchangeId, ExchangeDto exchangeDto) {
        Set<ConstraintViolation<ExchangeDto>> violations = validator.validate(exchangeDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            throw returnValidationException(LOGGER, violations);
        } else {
            Optional<Exchange> optionalExchange = exchangeRepository.findById(exchangeId);
            if (optionalExchange.isPresent()) {
                Exchange updatedExchange = exchangeMapper.updateEntityWithDto(exchangeDto, optionalExchange.get());
                exchangeRepository.save(updatedExchange);
                return exchangeMapper.convertToDto(updatedExchange);
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, exchangeId);
            }
        }
    }

    @Override
    public ExchangeDto updateExchangeBySuffix(String suffix, ExchangeDto exchangeDto) {
        validateRequestDto(exchangeDto);
        Optional<Exchange> optionalExchange = exchangeRepository.findBySuffixIgnoreCase(suffix);
        if (optionalExchange.isPresent()) {
            Exchange updatedExchange = exchangeMapper.updateEntityWithDto(exchangeDto, optionalExchange.get());
            exchangeRepository.save(updatedExchange);
            return exchangeMapper.convertToDto(updatedExchange);
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_SUFFIX, suffix);
        }
    }

    @Override
    @Transactional
    public void deleteAllExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        if (!exchanges.isEmpty()) {
            exchangeRepository.deleteAll();
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGES_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteExchangeById(Long exchangeId) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(exchangeId);
        if (optionalExchange.isPresent()) {
            exchangeRepository.deleteById(exchangeId);
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_ID, exchangeId);
        }
    }

    @Override
    @Transactional
    public void deleteExchangeBySuffix(String suffix) {
        Optional<Exchange> optionalExchange = exchangeRepository.findBySuffixIgnoreCase(suffix);
        if (optionalExchange.isPresent()) {
            exchangeRepository.deleteBySuffixIgnoreCase(suffix);
        } else {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_SUFFIX, suffix);
        }
    }

    private void validateRequestDto(ExchangeDto exchangeDto) {
        Set<ConstraintViolation<ExchangeDto>> violations = validator.validate(exchangeDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            throw returnValidationException(LOGGER, violations);
        }
    }
}