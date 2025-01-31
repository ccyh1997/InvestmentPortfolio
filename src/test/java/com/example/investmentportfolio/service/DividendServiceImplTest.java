package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.DividendDto;
import com.example.investmentportfolio.mapper.DividendMapper;
import com.example.investmentportfolio.model.Dividend;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.model.Stock;
import com.example.investmentportfolio.repository.DividendRepository;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.repository.StockRepository;
import com.example.investmentportfolio.service.impl.DividendServiceImpl;
import com.example.investmentportfolio.util.AlreadyExistsException;
import com.example.investmentportfolio.util.NotFoundException;
import com.example.investmentportfolio.util.ValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
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
class DividendServiceImplTest {
    @Mock
    private DividendRepository dividendRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private DividendMapper dividendMapper;

    @InjectMocks
    private DividendServiceImpl dividendService;

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void givenValidRequest_whenCreateDividend_thenCreateDividend() {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Dividend dividend = new Dividend();
        dividend.setStockTicker("D05");
        dividend.setExchange("SGX");
        dividend.setExDate("2023-08-17");
        dividend.setPayDate("2023-08-30");
        dividend.setPayout("0.0305");
        when(exchangeRepository.findIdByExchange(requestDividendDto.getExchange())).thenReturn(Optional.of(1L));
        when(dividendMapper.convertToEntity(requestDividendDto)).thenReturn(dividend);
        when(stockRepository.findIdByTickerAndExchangeId(requestDividendDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.of(1L));
        when(dividendRepository.existsByExDateOrPayDate(dividend.getExDate(), dividend.getPayDate())).thenReturn(false);
        dividendService.createDividend(requestDividendDto);
        verify(dividendRepository, times(1)).save(dividend);
    }

    @Test
    void givenBadRequest_whenCreateDividend_thenThrowValidationException() {
        DividendDto requestDividendDto = new DividendDto("D05", "", "2023-08-17", "2023-08-30", "0.0305");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            dividendService.createDividend(requestDividendDto);
        });
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenCreateDividend_thenThrowNotFoundException() {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(exchangeRepository.findIdByExchange(requestDividendDto.getExchange())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.createDividend(requestDividendDto);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockTickerDoesNotExist_whenCreateDividend_thenThrowNotFoundException() {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Dividend dividend = new Dividend();
        dividend.setStockTicker("D05");
        dividend.setExchange("SGX");
        dividend.setExDate("2023-08-17");
        dividend.setPayDate("2023-08-30");
        dividend.setPayout("0.0305");
        when(exchangeRepository.findIdByExchange(requestDividendDto.getExchange())).thenReturn(Optional.of(1L));
        when(dividendMapper.convertToEntity(requestDividendDto)).thenReturn(dividend);
        when(stockRepository.findIdByTickerAndExchangeId(requestDividendDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.createDividend(requestDividendDto);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker D05 cannot be found in exchange: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendAlreadyExists_whenCreateDividend_thenThrowAlreadyExistsException() {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Dividend dividend = new Dividend();
        dividend.setStockTicker("D05");
        dividend.setExchange("SGX");
        dividend.setExDate("2023-08-17");
        dividend.setPayDate("2023-08-30");
        dividend.setPayout("0.0305");
        when(exchangeRepository.findIdByExchange(requestDividendDto.getExchange())).thenReturn(Optional.of(1L));
        when(dividendMapper.convertToEntity(requestDividendDto)).thenReturn(dividend);
        when(stockRepository.findIdByTickerAndExchangeId(requestDividendDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.of(1L));
        when(dividendRepository.existsByExDateOrPayDate(dividend.getExDate(), dividend.getPayDate())).thenReturn(true);
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            dividendService.createDividend(requestDividendDto);
        });
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("A dividend with the same ex date or pay date already exists.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendsExist_whenGetAllDividends_thenReturnDividends() {
        Dividend dividend1 = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Dividend dividend2 = new Dividend(2L, 2L, "OV8", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock1 = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        Stock stock2 = new Stock(2L, "OV8", "Sheng Siong Group Ltd", "Equity", 1L, "SGX", "1.5306078", "SGD", "Y", "N");
        Exchange exchange = new Exchange(1L, "SGX", "SG", ",SI");
        List<Dividend> dividends = List.of(dividend1, dividend2);
        when(dividendRepository.findAll()).thenReturn(dividends);
        when(stockRepository.findById(dividend1.getStockId())).thenReturn(Optional.of(stock1));
        when(stockRepository.findById(dividend2.getStockId())).thenReturn(Optional.of(stock2));
        when(exchangeRepository.findById(dividend1.getExchangeId())).thenReturn(Optional.of(exchange));
        List<DividendDto> dividendDtos = dividendService.getAllDividends();
        assertEquals(2, dividendDtos.size());
    }

    @Test
    void givenStockTickerDoesNotExist_whenGetAllDividends_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findAll()).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getAllDividends();
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetAllDividends_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findAll()).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(dividend.getExchangeId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getAllDividends();
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendsDoNotExist_whenGetAllDividends_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
           dividendService.getAllDividends();
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No dividend(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendExists_whenGetDividendById_thenReturnDividend() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        Exchange exchange = new Exchange(1L, "SGX", "SG", ",SI");
        when(dividendRepository.findById(dividend.getDividendId())).thenReturn(Optional.of(dividend));
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(dividend.getExchangeId())).thenReturn(Optional.of(exchange));
        dividendService.getDividendById(1L);
        verify(dividendRepository, times(1)).findById(dividend.getDividendId());
        verify(stockRepository, times(1)).findById(dividend.getStockId());
        verify(exchangeRepository, times(1)).findById(dividend.getExchangeId());
    }

    @Test
    void givenStockTickerDoesNotExist_whenGetDividendById_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(dividendRepository.findById(dividend.getDividendId())).thenReturn(Optional.of(dividend));
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendById(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetDividendById_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(dividendRepository.findById(dividend.getDividendId())).thenReturn(Optional.of(dividend));
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(dividend.getExchangeId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendById(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendDoesNotExist_whenGetDividendById_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendById(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No dividend found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendExists_whenGetDividendByStockId_thenReturnDividend() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        Exchange exchange = new Exchange(1L, "SGX", "SG", ",SI");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findByStockId(dividend.getStockId())).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(dividend.getExchangeId())).thenReturn(Optional.of(exchange));
        dividendService.getDividendsByStockId(1L);
        verify(dividendRepository, times(1)).findByStockId(dividend.getDividendId());
        verify(stockRepository, times(1)).findById(dividend.getStockId());
        verify(exchangeRepository, times(1)).findById(dividend.getExchangeId());
    }

    @Test
    void givenStockTickerDoesNotExist_whenGetDividendByStockId_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findByStockId(dividend.getStockId())).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendsByStockId(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetDividendByStockId_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findByStockId(dividend.getStockId())).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(dividend.getExchangeId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendsByStockId(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendDoesNotExist_whenGetDividendByStockId_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendsByStockId(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No dividend(s) found with stock id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendsExist_whenGetDividendByExchangeId_thenReturnDividends() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        Exchange exchange = new Exchange(1L, "SGX", "SG", ",SI");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findByExchangeId(dividend.getExchangeId())).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(dividend.getExchangeId())).thenReturn(Optional.of(exchange));
        List<DividendDto> dividendDtos = dividendService.getDividendsByExchangeId(dividend.getExchangeId());
        assertEquals(1, dividendDtos.size());
    }

    @Test
    void givenStockTickerDoesNotExist_whenGetDividendByExchangeId_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findByExchangeId(dividend.getExchangeId())).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendsByExchangeId(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetDividendByExchangeId_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findByExchangeId(dividend.getExchangeId())).thenReturn(dividends);
        when(stockRepository.findById(dividend.getStockId())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(dividend.getExchangeId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendsByExchangeId(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendsDoNotExist_whenGetDividendByExchangeId_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.getDividendsByExchangeId(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No dividend(s) found with exchange id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendExists_whenUpdateDividendById_thenUpdateDividend() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        DividendDto dividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(dividendRepository.findById(dividend.getDividendId())).thenReturn(Optional.of(dividend));
        when(dividendMapper.updateEntityWithDto(dividendDto, dividend)).thenReturn(dividend);
        when(exchangeRepository.findIdByExchange(dividendDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(dividendDto.getStockTicker().toUpperCase(), dividend.getExchangeId())).thenReturn(Optional.of(1L));
        dividendService.updateDividendById(1L, dividendDto);
        verify(dividendRepository, times(1)).findById(dividend.getDividendId());
        verify(exchangeRepository, times(1)).findIdByExchange(dividendDto.getExchange());
        verify(stockRepository, times(1)).findIdByTickerAndExchangeId(dividendDto.getStockTicker().toUpperCase(), dividend.getExchangeId());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateDividendById_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        DividendDto dividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(dividendRepository.findById(dividend.getDividendId())).thenReturn(Optional.of(dividend));
        when(exchangeRepository.findIdByExchange(dividendDto.getExchange())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.updateDividendById(1L, dividendDto);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockTickerDoesNotExist_whenUpdateDividendById_thenThrowNotFoundException() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        DividendDto dividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(dividendRepository.findById(dividend.getDividendId())).thenReturn(Optional.of(dividend));
        when(dividendMapper.updateEntityWithDto(dividendDto, dividend)).thenReturn(dividend);
        when(exchangeRepository.findIdByExchange(dividendDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(dividendDto.getStockTicker().toUpperCase(), dividend.getExchangeId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.updateDividendById(1L, dividendDto);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker D05 cannot be found in exchange: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendDoesNotExist_whenUpdateDividendById_thenThrowNotFoundException() {
        DividendDto dividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.updateDividendById(1L, dividendDto);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No dividend found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendsExist_whenDeleteAllDividends_thenDeleteDividends() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        List<Dividend> dividends = List.of(dividend);
        when(dividendRepository.findAll()).thenReturn(dividends);
        dividendService.deleteAllDividends();
        verify(dividendRepository, times(1)).deleteAll();
    }

    @Test
    void givenDividendsDoNotExist_whenDeleteAllDividends_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.deleteAllDividends();
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No dividend(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenDividendExists_whenDeleteDividendById_thenDeleteDividend() {
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(dividendRepository.findById(dividend.getDividendId())).thenReturn(Optional.of(dividend));
        dividendService.deleteDividendById(dividend.getDividendId());
        verify(dividendRepository, times(1)).deleteById(dividend.getDividendId());
    }

    @Test
    void givenDividendDoesNotExist_whenDeleteDividendById_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dividendService.deleteDividendById(1L);
        });
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No dividend found with id: 1", exception.getError().getErrorMessages().getFirst());
    }
}
