package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.mapper.ExchangeMapper;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.service.ExchangeService;
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
public class ExchangeServiceImpl implements ExchangeService {
    private static final Logger LOGGER = LogManager.getLogger(ExchangeServiceImpl.class);
    public static final String NO_EXCHANGE_FOUND_WITH_ID = "No exchange found with id: %d";
    public static final String NO_EXCHANGE_FOUND_WITH_SUFFIX = "No exchange found with suffix: %s";
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
        Set<ConstraintViolation<ExchangeDto>> violations = validator.validate(exchangeDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                            .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        }
        else {
            Exchange exchange = exchangeMapper.convertToEntity(exchangeDto);
            if (exchangeRepository.existsByExchangeOrSuffixIgnoreCase(exchange.getExchange(), exchange.getSuffix())) {
                List<String> errorMessages = Collections.singletonList("An exchange with the same name or suffix already exists.");
                LOGGER.error(errorMessages);
                throw new AlreadyExistsException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
            } else {
                exchangeRepository.save(exchange);
                return exchangeMapper.convertToDto(exchange);
            }
        }
    }

    @Override
    public List<ExchangeDto> getAllExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        if (!exchanges.isEmpty()) {
            return exchanges.stream()
                    .map(exchangeMapper::convertToDto)
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No exchange(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public ExchangeDto getExchangeById(Long exchangeId) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(exchangeId);
        if (optionalExchange.isPresent()) {
            return exchangeMapper.convertToDto(optionalExchange.get());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_ID, exchangeId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<ExchangeDto> getExchangesByCountryCode(String countryCode) {
        List<Exchange> exchanges = exchangeRepository.findByCountryCodeIgnoreCase(countryCode);
        if (!exchanges.isEmpty()) {
            return exchanges.stream()
                    .map(exchangeMapper::convertToDto)
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No exchange(s) found with country code: %s", countryCode));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public ExchangeDto getExchangeBySuffix(String suffix) {
        Optional<Exchange> optionalExchange = exchangeRepository.findBySuffixIgnoreCase(suffix);
        if (optionalExchange.isPresent()) {
            return exchangeMapper.convertToDto(optionalExchange.get());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_SUFFIX, suffix));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public ExchangeDto updateExchangeById(Long exchangeId, ExchangeDto exchangeDto) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(exchangeId);
        if (optionalExchange.isPresent()) {
            Exchange updatedExchange = exchangeMapper.updateEntityWithDto(exchangeDto, optionalExchange.get());
            exchangeRepository.save(updatedExchange);
            return exchangeMapper.convertToDto(updatedExchange);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_ID, exchangeId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public ExchangeDto updateExchangeBySuffix(String suffix, ExchangeDto exchangeDto) {
        Optional<Exchange> optionalExchange = exchangeRepository.findBySuffixIgnoreCase(suffix);
        if (optionalExchange.isPresent()) {
            Exchange updatedExchange = exchangeMapper.updateEntityWithDto(exchangeDto, optionalExchange.get());
            exchangeRepository.save(updatedExchange);
            return exchangeMapper.convertToDto(updatedExchange);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_SUFFIX, suffix));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteAllExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        if (!exchanges.isEmpty()) {
            exchangeRepository.deleteAll();
        } else {
            List<String> errorMessages = Collections.singletonList("No exchange(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteExchangeById(Long exchangeId) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(exchangeId);
        if (optionalExchange.isPresent()) {
            exchangeRepository.deleteById(exchangeId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_ID, exchangeId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public ExchangeDto deleteExchangeBySuffix(String suffix) {
        Optional<Exchange> optionalExchange = exchangeRepository.findBySuffixIgnoreCase(suffix);
        if (optionalExchange.isPresent()) {
             exchangeRepository.deleteBySuffixIgnoreCase(suffix);
             return exchangeMapper.convertToDto(optionalExchange.get());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_EXCHANGE_FOUND_WITH_SUFFIX, suffix));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }
}
