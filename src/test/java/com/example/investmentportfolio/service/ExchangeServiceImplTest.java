package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.mapper.ExchangeMapper;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.service.impl.ExchangeServiceImpl;
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
class ExchangeServiceImplTest {

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private ExchangeMapper exchangeMapper;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;
    
    @Test
    void givenValidRequest_whenCreateExchange_thenCreateExchange() {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        Exchange exchange = new Exchange();
        exchange.setExchange("SEHK");
        exchange.setCountryCode("HK");
        exchange.setSuffix(".HK");
        when(exchangeMapper.convertToEntity(requestExchangeDto)).thenReturn(exchange);
        exchangeService.createExchange(requestExchangeDto);
        verify(exchangeRepository, times(1)).save(exchange);
    }

    @Test
    void givenBadRequest_whenCreateExchange_thenThrowValidationException() {
        ExchangeDto requestExchangeDto = new ExchangeDto("", "HK", ".HK");
        ValidationException exception = assertThrows(ValidationException.class, () -> exchangeService.createExchange(requestExchangeDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeAlreadyExists_whenCreateExchange_thenThrowAlreadyExistsException() {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        Exchange exchange = new Exchange();
        exchange.setExchange("SEHK");
        exchange.setCountryCode("HK");
        exchange.setSuffix(".HK");
        when(exchangeMapper.convertToEntity(requestExchangeDto)).thenReturn(exchange);
        when(exchangeRepository.existsByExchangeOrSuffixIgnoreCase(exchange.getExchange(), exchange.getSuffix())).thenReturn(true);
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> exchangeService.createExchange(requestExchangeDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("An exchange with the same name or suffix already exists.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangesExist_whenGetAllExchanges_thenReturnExchanges() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        List<Exchange> exchanges = List.of(exchange);
        when(exchangeRepository.findAll()).thenReturn(exchanges);
        List<ExchangeDto> exchangeDtos = exchangeService.getAllExchanges();
        assertEquals(1, exchangeDtos.size());
    }

    @Test
    void givenExchangesDoNotExist_whenGetAllExchanges_thenThrowNotFoundException() {
        when(exchangeRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.getAllExchanges());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeExists_whenGetExchangeById_thenReturnExchange() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        when(exchangeRepository.findById(exchange.getExchangeId())).thenReturn(Optional.of(exchange));
        exchangeService.getExchangeById(exchange.getExchangeId());
        verify(exchangeRepository, times(1)).findById(exchange.getExchangeId());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetExchangeById_thenThrowNotFoundException() {
        when(exchangeRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.getExchangeById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangesExist_whenGetExchangesByCountryCode_thenReturnExchanges() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        List<Exchange> exchanges = List.of(exchange);
        when(exchangeRepository.findByCountryCodeIgnoreCase(exchange.getCountryCode())).thenReturn(exchanges);
        List<ExchangeDto> exchangeDtos = exchangeService.getExchangesByCountryCode(exchange.getCountryCode());
        assertEquals(1, exchangeDtos.size());
    }

    @Test
    void givenExchangesDoNotExist_whenGetExchangesByCountryCode_thenThrowNotFoundException() {
        when(exchangeRepository.findByCountryCodeIgnoreCase("SGX")).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.getExchangesByCountryCode("SGX"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange(s) found with country code: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeExists_whenGetExchangeBySuffix_thenReturnExchange() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        when(exchangeRepository.findBySuffixIgnoreCase(exchange.getSuffix())).thenReturn(Optional.of(exchange));
        exchangeService.getExchangeBySuffix(exchange.getSuffix());
        verify(exchangeRepository, times(1)).findBySuffixIgnoreCase(exchange.getSuffix());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetExchangeBySuffix_thenThrowNotFoundException() {
        when(exchangeRepository.findBySuffixIgnoreCase(".SI")).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.getExchangeBySuffix(".SI"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with suffix: .SI", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequest_whenUpdateExchangeById_thenUpdateExchange() {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        Exchange exchange = new Exchange();
        exchange.setExchange("SEHK");
        exchange.setCountryCode("HK");
        exchange.setSuffix(".HK");
        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(exchange));
        when(exchangeMapper.updateEntityWithDto(requestExchangeDto, exchange)).thenReturn(exchange);
        exchangeService.updateExchangeById(1L, requestExchangeDto);
        verify(exchangeRepository, times(1)).save(exchange);
    }

    @Test
    void givenBadRequest_whenUpdateExchangeById_thenThrowValidationException() {
        ExchangeDto requestExchangeDto = new ExchangeDto("", "HK", ".HK");
        ValidationException exception = assertThrows(ValidationException.class, () -> exchangeService.updateExchangeById(1L, requestExchangeDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateExchangeById_thenThrowNotFoundException() {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        when(exchangeRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.updateExchangeById(1L, requestExchangeDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequest_whenUpdateExchangeBySuffix_thenUpdateExchange() {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        Exchange exchange = new Exchange();
        exchange.setExchange("SEHK");
        exchange.setCountryCode("HK");
        exchange.setSuffix(".HK");
        when(exchangeRepository.findBySuffixIgnoreCase(requestExchangeDto.getSuffix())).thenReturn(Optional.of(exchange));
        when(exchangeMapper.updateEntityWithDto(requestExchangeDto, exchange)).thenReturn(exchange);
        exchangeService.updateExchangeBySuffix(requestExchangeDto.getSuffix(), requestExchangeDto);
        verify(exchangeRepository, times(1)).save(exchange);
    }

    @Test
    void givenBadRequest_whenUpdateExchangeBySuffix_thenThrowValidationException() {
        ExchangeDto requestExchangeDto = new ExchangeDto("", "HK", ".HK");
        ValidationException exception = assertThrows(ValidationException.class, () -> exchangeService.updateExchangeBySuffix(".HK", requestExchangeDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateExchangeBySuffix_thenThrowNotFoundException() {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        when(exchangeRepository.findBySuffixIgnoreCase(requestExchangeDto.getSuffix())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.updateExchangeBySuffix(".HK", requestExchangeDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with suffix: .HK", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangesExist_whenDeleteAllExchanges_thenDeleteExchanges() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        List<Exchange> exchanges = List.of(exchange);
        when(exchangeRepository.findAll()).thenReturn(exchanges);
        exchangeService.deleteAllExchanges();
        verify(exchangeRepository, times(1)).deleteAll();
    }

    @Test
    void givenExchangesDoNotExist_whenDeleteAllExchanges_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.deleteAllExchanges());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeExists_whenDeleteExchangeById_thenDeleteExchange() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        when(exchangeRepository.findById(exchange.getExchangeId())).thenReturn(Optional.of(exchange));
        exchangeService.deleteExchangeById(exchange.getExchangeId());
        verify(exchangeRepository, times(1)).deleteById(exchange.getExchangeId());
    }

    @Test
    void givenExchangeDoesNotExist_whenDeleteExchangeById_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.deleteExchangeById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeExists_whenDeleteExchangeBySuffix_thenDeleteExchange() {
        Exchange exchange = new Exchange(1L, "SGX", "SG", ".SI");
        when(exchangeRepository.findBySuffixIgnoreCase(exchange.getSuffix())).thenReturn(Optional.of(exchange));
        exchangeService.deleteExchangeBySuffix(exchange.getSuffix());
        verify(exchangeRepository, times(1)).deleteBySuffixIgnoreCase(exchange.getSuffix());
    }

    @Test
    void givenExchangeDoesNotExist_whenDeleteExchangeBySuffix_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> exchangeService.deleteExchangeBySuffix(".SI"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with suffix: .SI", exception.getError().getErrorMessages().getFirst());
    }
}