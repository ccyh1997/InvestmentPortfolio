package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.mapper.RateMapper;
import com.example.investmentportfolio.model.Rate;
import com.example.investmentportfolio.repository.RateRepository;
import com.example.investmentportfolio.service.RateService;
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
public class RateServiceImpl implements RateService {
    private static final Logger LOGGER = LogManager.getLogger(RateServiceImpl.class);
    public static final String NO_RATE_FOUND_WITH_ID = "No rate found with id: %d";
    private final RateRepository rateRepository;
    private final RateMapper rateMapper;
    private final Validator validator;

    public RateServiceImpl(RateRepository rateRepository, RateMapper rateMapper) {
        this.rateRepository = rateRepository;
        this.rateMapper = rateMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public RateDto createRate(RateDto rateDto) {
        Set<ConstraintViolation<RateDto>> violations = validator.validate(rateDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        }
        else {
            Rate rate = rateMapper.convertToEntity(rateDto);
            if (rateRepository.existsByRateNameIgnoreCase(rate.getRateName())) {
                List<String> errorMessages = Collections.singletonList("A rate with the same name already exists.");
                LOGGER.error(errorMessages);
                throw new AlreadyExistsException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
            } else {
                rateRepository.save(rate);
                return rateMapper.convertToDto(rate);
            }
        }
    }

    @Override
    public List<RateDto> getAllRates() {
        List<Rate> rates = rateRepository.findAll();
        if (!rates.isEmpty()) {
            return rates.stream()
                    .map(rateMapper::convertToDto)
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No rate(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public RateDto getRateById(Long rateId) {
        Optional<Rate> optionalRate = rateRepository.findById(rateId);
        if (optionalRate.isPresent()) {
            return rateMapper.convertToDto(optionalRate.get());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_RATE_FOUND_WITH_ID, rateId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public RateDto updateRateById(Long rateId, RateDto rateDto) {
        Optional<Rate> optionalRate = rateRepository.findById(rateId);
        if (optionalRate.isPresent()) {
            Rate updatedRate = rateMapper.updateEntityWithDto(rateDto, optionalRate.get());
            rateRepository.save(updatedRate);
            return rateMapper.convertToDto(updatedRate);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_RATE_FOUND_WITH_ID, rateId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteAllRates() {
        List<Rate> rates = rateRepository.findAll();
        if (!rates.isEmpty()) {
            rateRepository.deleteAll();
        } else {
            List<String> errorMessages = Collections.singletonList("No rate(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteRateById(Long rateId) {
        Optional<Rate> optionalRate = rateRepository.findById(rateId);
        if (optionalRate.isPresent()) {
            rateRepository.deleteById(rateId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_RATE_FOUND_WITH_ID, rateId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }
}
