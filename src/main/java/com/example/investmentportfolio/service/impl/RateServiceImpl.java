package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.mapper.RateMapper;
import com.example.investmentportfolio.model.Rate;
import com.example.investmentportfolio.repository.RateRepository;
import com.example.investmentportfolio.service.RateService;
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
public class RateServiceImpl implements RateService {
    private static final Logger LOGGER = LogManager.getLogger(RateServiceImpl.class);
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
        validateRequestDto(rateDto);
        Rate rate = rateMapper.convertToEntity(rateDto);
        if (rateRepository.existsByRateNameIgnoreCase(rate.getRateName())) {
            throw returnAlreadyExistsException(LOGGER, RATE_ALREADY_EXISTS);
        } else {
            rateRepository.save(rate);
            return rateMapper.convertToDto(rate);
        }
    }

    @Override
    public List<RateDto> getAllRates() {
        List<Rate> rates = rateRepository.findAll();
        if (!rates.isEmpty()) {
            return rates.stream().map(rateMapper::convertToDto).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_RATES_FOUND);
        }
    }

    @Override
    public RateDto getRateById(Long rateId) {
        Optional<Rate> optionalRate = rateRepository.findById(rateId);
        if (optionalRate.isPresent()) {
            return rateMapper.convertToDto(optionalRate.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_RATE_FOUND_WITH_ID, rateId);
        }
    }

    @Override
    public RateDto updateRateById(Long rateId, RateDto rateDto) {
        validateRequestDto(rateDto);
        Optional<Rate> optionalRate = rateRepository.findById(rateId);
        if (optionalRate.isPresent()) {
            Rate updatedRate = rateMapper.updateEntityWithDto(rateDto, optionalRate.get());
            rateRepository.save(updatedRate);
            return rateMapper.convertToDto(updatedRate);
        } else {
            throw returnNotFoundException(LOGGER, NO_RATE_FOUND_WITH_ID, rateId);
        }
    }

    @Override
    @Transactional
    public void deleteAllRates() {
        List<Rate> rates = rateRepository.findAll();
        if (!rates.isEmpty()) {
            rateRepository.deleteAll();
        } else {
            throw returnNotFoundException(LOGGER, NO_RATES_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteRateById(Long rateId) {
        Optional<Rate> optionalRate = rateRepository.findById(rateId);
        if (optionalRate.isPresent()) {
            rateRepository.deleteById(rateId);
        } else {
            throw returnNotFoundException(LOGGER, NO_RATE_FOUND_WITH_ID, rateId);
        }
    }

    private void validateRequestDto(RateDto rateDto) {
        Set<ConstraintViolation<RateDto>> violations = validator.validate(rateDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            throw returnValidationException(LOGGER, violations);
        }
    }
}
