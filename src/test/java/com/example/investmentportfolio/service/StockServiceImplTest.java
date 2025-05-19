package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.mapper.StockMapper;
import com.example.investmentportfolio.model.Exchange;
import com.example.investmentportfolio.model.Stock;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.repository.StockRepository;
import com.example.investmentportfolio.service.impl.StockServiceImpl;
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
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private StockMapper stockMapper;
    
    @Mock
    Validator validator;

    @InjectMocks
    StockServiceImpl stockService;
    
    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    @Test
    void givenValidRequest_whenCreateStock_thenCreateStock() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockMapper.convertToEntity(requestStockDto)).thenReturn(stock);
        when(exchangeRepository.findIdByExchange(requestStockDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockRepository.existsByStockTickerIgnoreCase(requestStockDto.getStockTicker().toUpperCase())).thenReturn(false);
        stockService.createStock(requestStockDto);
        verify(stockRepository, times(1)).save(any());
    }

    @Test
    void givenBadRequest_whenCreateStock_thenThrowValidationException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "", "43.62", "SGD", "Y", "N");
        ValidationException exception = assertThrows(ValidationException.class, () -> stockService.createStock(requestStockDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenCreateStock_thenThrowNotFoundException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockMapper.convertToEntity(requestStockDto)).thenReturn(stock);
        when(exchangeRepository.findIdByExchange(requestStockDto.getExchange())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.createStock(requestStockDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockAlreadyExists_whenCreateStock_thenThrowAlreadyExistsException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockMapper.convertToEntity(requestStockDto)).thenReturn(stock);
        when(exchangeRepository.findIdByExchange(requestStockDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockRepository.existsByStockTickerIgnoreCase(requestStockDto.getStockTicker().toUpperCase())).thenReturn(true);
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> stockService.createStock(requestStockDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("A stock with the same ticker already exists.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStocksExist_whenGetAllStocks_thenReturnStocks() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Stock> stocks = List.of(stock);
        Exchange exchange = new Exchange();
        when(stockRepository.findAll()).thenReturn(stocks);
        when(exchangeRepository.findById(any())).thenReturn(Optional.of(exchange));
        when(stockMapper.convertToDto(stock)).thenReturn(requestStockDto);
        List<StockDto> retrievedStocks = stockService.getAllStocks();
        assertEquals(1, retrievedStocks.size());
    }

    @Test
    void givenStocksDoNotExist_whenGetAllStocks_thenThrowNotFoundException() {
        when(stockRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getAllStocks());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetAllStocks_thenThrowNotFoundException() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Stock> stocks = List.of(stock);
        when(stockRepository.findAll()).thenReturn(stocks);
        when(exchangeRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getAllStocks());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockExists_whenGetStockById_thenReturnStock() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        Exchange exchange = new Exchange();
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(any())).thenReturn(Optional.of(exchange));
        when(stockMapper.convertToDto(stock)).thenReturn(requestStockDto);
        stockService.getStockById(any());
        verify(stockMapper, times(1)).convertToDto(any());
    }

    @Test
    void givenStockDoesNotExist_whenGetStockById_thenThrowNotFoundException() {
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getStockById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetStockById_thenThrowNotFoundException() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getStockById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockExists_whenGetStockByTicker_thenReturnStock() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        Exchange exchange = new Exchange();
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(any())).thenReturn(Optional.of(exchange));
        when(stockMapper.convertToDto(stock)).thenReturn(requestStockDto);
        stockService.getStockByTicker(any());
        verify(stockMapper, times(1)).convertToDto(any());
    }

    @Test
    void givenStockDoesNotExist_whenGetStockByTicker_thenThrowNotFoundException() {
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getStockByTicker("SGX"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with ticker: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetStockByTicker_thenThrowNotFoundException() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.of(stock));
        when(exchangeRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getStockByTicker("SGX"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStocksExist_whenGetStocksByFilters_thenReturnStocks() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Stock> stocks = List.of(stock);
        Exchange exchange = new Exchange();
        when(stockRepository.findByFilters(any(), any(), any(), any())).thenReturn(stocks);
        when(exchangeRepository.findById(any())).thenReturn(Optional.of(exchange));
        when(stockMapper.convertToDto(stock)).thenReturn(requestStockDto);
        List<StockDto> retrievedStocks = stockService.getStocksByFilters(any(), any(), any(), any());
        assertEquals(1, retrievedStocks.size());
    }

    @Test
    void givenStocksDoNotExist_whenGetStocksByFilters_thenThrowNotFoundException() {
        when(stockRepository.findByFilters(any(), any(), any(), any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getStocksByFilters(1L, "Equity", "Y", "N"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks were found matching the provided filters.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetStocksByFilters_thenThrowNotFoundException() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Stock> stocks = List.of(stock);
        when(stockRepository.findByFilters(any(), any(), any(), any())).thenReturn(stocks);
        when(exchangeRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.getStocksByFilters(1L, "Equity", "Y", "N"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequest_whenUpdateStockById_thenUpdateStock() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockMapper.updateEntityWithDto(any(), any())).thenReturn(stock);
        when(exchangeRepository.findIdByExchange(requestStockDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockMapper.convertToDto(stock)).thenReturn(requestStockDto);
        stockService.updateStockById(any(), requestStockDto);
        verify(stockRepository, times(1)).save(any());
    }

    @Test
    void givenBadRequest_whenUpdateStockById_thenThrowValidationException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "", "43.62", "SGD", "Y", "N");
        ValidationException exception = assertThrows(ValidationException.class, () -> stockService.updateStockById(1L, requestStockDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateStockById_thenThrowNotFoundException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockMapper.updateEntityWithDto(any(), any())).thenReturn(stock);
        when(exchangeRepository.findIdByExchange(requestStockDto.getExchange())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.updateStockById(1L, requestStockDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenUpdateStockById_thenThrowNotFoundException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.updateStockById(1L, requestStockDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequest_whenUpdateStockByTicker_thenUpdateStock() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.of(stock));
        when(stockMapper.updateEntityWithDto(any(), any())).thenReturn(stock);
        when(exchangeRepository.findIdByExchange(requestStockDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockMapper.convertToDto(stock)).thenReturn(requestStockDto);
        stockService.updateStockByTicker(any(), requestStockDto);
        verify(stockRepository, times(1)).save(any());
    }

    @Test
    void givenBadRequest_whenUpdateStockByTicker_thenThrowValidationException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "", "43.62", "SGD", "Y", "N");
        ValidationException exception = assertThrows(ValidationException.class, () -> stockService.updateStockByTicker("SGX", requestStockDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateStockByTicker_thenThrowNotFoundException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.of(stock));
        when(stockMapper.updateEntityWithDto(any(), any())).thenReturn(stock);
        when(exchangeRepository.findIdByExchange(requestStockDto.getExchange())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.updateStockByTicker("SGX", requestStockDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenUpdateStockByTicker_thenThrowNotFoundException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.updateStockByTicker("SGX", requestStockDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with ticker: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStocksExist_whenDeleteAllStocks_thenDeleteStocks() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Stock> stocks = List.of(stock);
        when(stockRepository.findAll()).thenReturn(stocks);
        stockService.deleteAllStocks();
        verify(stockRepository, times(1)).deleteAll();
    }

    @Test
    void givenStocksDoNotExist_whenDeleteStockById_thenThrowNotFoundException() {
        when(stockRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.deleteAllStocks());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockExists_whenDeleteStockById_thenDeleteStock() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        stockService.deleteStockById(1L);
        verify(stockRepository, times(1)).deleteById(any());
    }

    @Test
    void givenStockDoesNotExist_whenDeleteStockById_thenThrowNotFoundException() {
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.deleteStockById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockExists_whenDeleteStockByTicker_thenDeleteStock() {
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.of(stock));
        stockService.deleteStockByTicker("D05");
        verify(stockRepository, times(1)).deleteByStockTickerIgnoreCase(any());
    }

    @Test
    void givenStockDoesNotExist_whenDeleteStockByTicker_thenThrowNotFoundException() {
        when(stockRepository.findByStockTickerIgnoreCase(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.deleteStockByTicker("SGX"));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with ticker: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateLiveStockPrices_thenThrowNotFoundException() {
        StockDto requestStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        Stock stock = new Stock(1L, "D05", "DBS Group Holdings Ltd", "Equity", 1L, "SGX", "43.62", "SGD", "Y", "N");
        List<Stock> stocks = List.of(stock);
        Exchange exchange = new Exchange();
        when(stockRepository.findAll()).thenReturn(stocks);
        when(exchangeRepository.findById(any())).thenReturn(Optional.of(exchange));
        when(stockMapper.convertToDto(stock)).thenReturn(requestStockDto);
        when(exchangeRepository.findSuffixByExchange(any())).thenReturn(null);
        when(exchangeRepository.findIdByExchange(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stockService.updateLiveStockPrices());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock with ticker D05 cannot be found in exchange: SGX", exception.getError().getErrorMessages().getFirst());
    }
}