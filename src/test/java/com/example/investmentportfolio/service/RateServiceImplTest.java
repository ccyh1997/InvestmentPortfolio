package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.mapper.RateMapper;
import com.example.investmentportfolio.model.Rate;
import com.example.investmentportfolio.repository.RateRepository;
import com.example.investmentportfolio.service.impl.RateServiceImpl;
import com.example.investmentportfolio.util.AlreadyExistsException;
import com.example.investmentportfolio.util.NotFoundException;
import com.example.investmentportfolio.util.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.investmentportfolio.util.Constants.BAD_REQUEST_ERROR_CODE;
import static com.example.investmentportfolio.util.Constants.NOT_FOUND_ERROR_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateServiceImplTest {

    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateMapper rateMapper;

    @InjectMocks
    private RateServiceImpl rateService;

    
    @Test
    void givenValidRequest_whenCreateRate_thenCreateRate() {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        Rate rate = new Rate();
        rate.setRateName("SGD/USD");
        rate.setRate("0.74120251");
        when(rateMapper.convertToEntity(requestRateDto)).thenReturn(rate);
        rateService.createRate(requestRateDto);
        verify(rateRepository, times(1)).save(rate);
    }

    @Test
    void givenBadRequest_whenCreateRate_thenThrowValidationException() {
        RateDto requestRateDto = new RateDto("SGD/USD", "");
        ValidationException exception = assertThrows(ValidationException.class, () -> rateService.createRate(requestRateDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Rate cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenRateAlreadyExists_whenCreateRate_thenThrowAlreadyExistsException() {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        Rate rate = new Rate();
        rate.setRateName("SGD/USD");
        rate.setRate("0.74120251");
        when(rateMapper.convertToEntity(requestRateDto)).thenReturn(rate);
        when(rateRepository.existsByRateNameIgnoreCase(rate.getRateName())).thenReturn(true);
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> rateService.createRate(requestRateDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("A rate with the same name already exists.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenRatesExist_whenGetAllRates_thenReturnRates() {
        Rate rate = new Rate(1L, "SGD/USD", "0.74120251");
        List<Rate> rates = List.of(rate);
        when(rateRepository.findAll()).thenReturn(rates);
        List<RateDto> rateDtos = rateService.getAllRates();
        assertEquals(1, rateDtos.size());
    }

    @Test
    void givenRatesDoNotExist_whenGetAllRates_thenThrowNotFoundException() {
        when(rateRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> rateService.getAllRates());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenRateExists_whenGetRateById_thenReturnRate() {
        Rate rate = new Rate(1L, "SGD/USD", "0.74120251");
        when(rateRepository.findById(rate.getRateId())).thenReturn(Optional.of(rate));
        rateService.getRateById(rate.getRateId());
        verify(rateRepository, times(1)).findById(rate.getRateId());
    }

    @Test
    void givenRateDoesNotExist_whenGetRateById_thenThrowNotFoundException() {
        when(rateRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> rateService.getRateById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequest_whenUpdateRateById_thenUpdateRate() {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        Rate rate = new Rate();
        rate.setRateName("SGD/USD");
        rate.setRate("0.74120251");
        when(rateRepository.findById(1L)).thenReturn(Optional.of(rate));
        when(rateMapper.updateEntityWithDto(requestRateDto, rate)).thenReturn(rate);
        rateService.updateRateById(1L, requestRateDto);
        verify(rateRepository, times(1)).save(rate);
    }

    @Test
    void givenBadRequest_whenUpdateRateById_thenThrowValidationException() {
        RateDto requestRateDto = new RateDto("SGD/USD", "");
        ValidationException exception = assertThrows(ValidationException.class, () -> rateService.updateRateById(1L, requestRateDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Rate cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenRateDoesNotExist_whenUpdateRateById_thenThrowNotFoundException() {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        when(rateRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> rateService.updateRateById(1L, requestRateDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenRatesExist_whenDeleteAllRates_thenDeleteRates() {
        Rate rate = new Rate(1L, "SGD/USD", "0.74120251");
        List<Rate> rates = List.of(rate);
        when(rateRepository.findAll()).thenReturn(rates);
        rateService.deleteAllRates();
        verify(rateRepository, times(1)).deleteAll();
    }

    @Test
    void givenRatesDoNotExist_whenDeleteAllRates_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> rateService.deleteAllRates());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenRateExists_whenDeleteRateById_thenDeleteRate() {
        Rate rate = new Rate(1L, "SGD/USD", "0.74120251");
        when(rateRepository.findById(rate.getRateId())).thenReturn(Optional.of(rate));
        rateService.deleteRateById(rate.getRateId());
        verify(rateRepository, times(1)).deleteById(rate.getRateId());
    }

    @Test
    void givenRateDoesNotExist_whenDeleteRateById_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> rateService.deleteRateById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate found with id: 1", exception.getError().getErrorMessages().getFirst());
    }
}
