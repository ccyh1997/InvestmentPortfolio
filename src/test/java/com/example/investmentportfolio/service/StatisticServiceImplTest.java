package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.StatisticDto;
import com.example.investmentportfolio.mapper.StatisticMapper;
import com.example.investmentportfolio.model.*;
import com.example.investmentportfolio.repository.*;
import com.example.investmentportfolio.service.impl.StatisticServiceImpl;
import com.example.investmentportfolio.util.AlreadyExistsException;
import com.example.investmentportfolio.util.GeneralException;
import com.example.investmentportfolio.util.NotFoundException;
import com.example.investmentportfolio.util.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.investmentportfolio.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {

    @Mock
    private StatisticRepository statisticRepository;

    @Mock
    private DividendRepository dividendRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RateRepository rateRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private StatisticMapper statisticMapper;

    @InjectMocks
    private StatisticServiceImpl statisticService;

    @Test
    void givenValidRequest_whenCreateStatistic_thenCreateStatistic() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticMapper.convertToEntity(requestStatisticDto)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestStatisticDto.getExchange().toUpperCase())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(requestStatisticDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.of(1L));
        when(statisticRepository.existsByUserIdAndStockId(1L, 1L)).thenReturn(false);
        statisticService.createStatistic(requestStatisticDto);
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    void givenBadRequest_whenCreateStatistic_thenThrowValidationException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        ValidationException exception = assertThrows(ValidationException.class, () -> statisticService.createStatistic(requestStatisticDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUserDoesNotExist_whenCreateStatistic_thenThrowNotFoundException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticMapper.convertToEntity(requestStatisticDto)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.createStatistic(requestStatisticDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with username: testUser", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenCreateStatistic_thenThrowNotFoundException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticMapper.convertToEntity(requestStatisticDto)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestStatisticDto.getExchange().toUpperCase())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.createStatistic(requestStatisticDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockTickerDoesNotExist_whenCreateStatistic_thenThrowNotFoundException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticMapper.convertToEntity(requestStatisticDto)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestStatisticDto.getExchange().toUpperCase())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(requestStatisticDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.createStatistic(requestStatisticDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker D05 cannot be found in exchange: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticAlreadyExists_whenCreateStatistic_thenThrowAlreadyExistsException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticMapper.convertToEntity(requestStatisticDto)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestStatisticDto.getExchange().toUpperCase())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(requestStatisticDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.of(1L));
        when(statisticRepository.existsByUserIdAndStockId(1L, 1L)).thenReturn(true);
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> statisticService.createStatistic(requestStatisticDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("A statistic for this user with the same ticker and exchange already exists.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticsExist_whenGetAllStatistics_thenReturnStatistics() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        Stock stock = new Stock();
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findAll()).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(statistic.getStockId())).thenReturn(Optional.of("SGX"));
        statisticService.getAllStatistics();
        verify(statisticMapper, times(1)).convertToDto(statistic);
    }

    @Test
    void givenUserDoesNotExist_whenGetAllStatistics_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findAll()).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getAllStatistics());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenGetAllStatistics_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findAll()).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getAllStatistics());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetAllStatistics_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        Stock stock = new Stock();
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findAll()).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(statistic.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getAllStatistics());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with stock id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticsDoNotExist_whenGetAllStatistics_thenThrowNotFoundException() {
        List<Statistic> statistics = List.of();
        when(statisticRepository.findAll()).thenReturn(statistics);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getAllStatistics());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No statistic(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticExists_whenGetStatisticById_thenReturnStatistic() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        Stock stock = new Stock();
        when(statisticRepository.findById(statistic.getStatisticId())).thenReturn(Optional.of(statistic));
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(statistic.getStockId())).thenReturn(Optional.of("SGX"));
        statisticService.getStatisticById(statistic.getStatisticId());
        verify(statisticMapper, times(1)).convertToDto(statistic);
    }

    @Test
    void givenUserDoesNotExist_whenGetStatisticById_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticRepository.findById(statistic.getStatisticId())).thenReturn(Optional.of(statistic));
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenGetStatisticById_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        when(statisticRepository.findById(statistic.getStatisticId())).thenReturn(Optional.of(statistic));
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetStatisticById_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        Stock stock = new Stock();
        when(statisticRepository.findById(statistic.getStatisticId())).thenReturn(Optional.of(statistic));
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(statistic.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with stock id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticDoesNotExist_whenGetStatisticById_thenThrowNotFoundException() {
        when(statisticRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No statistic found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticsExist_whenGetStatisticsByUserId_thenReturnStatistics() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        Stock stock = new Stock();
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findByUserId(statistic.getUserId())).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(statistic.getStockId())).thenReturn(Optional.of("SGX"));
        statisticService.getStatisticsByUserId(statistic.getUserId());
        verify(statisticMapper, times(1)).convertToDto(statistic);
    }

    @Test
    void givenUserDoesNotExist_whenGetStatisticsByUserId_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findByUserId(statistic.getUserId())).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenGetStatisticsByUserId_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findByUserId(statistic.getUserId())).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetStatisticsByUserId_thenThrowNotFoundException() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        User user = new User();
        Stock stock = new Stock();
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findByUserId(statistic.getUserId())).thenReturn(statistics);
        when(userRepository.findById(statistic.getUserId())).thenReturn(Optional.of(user));
        when(stockRepository.findById(statistic.getStockId())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(statistic.getStockId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with stock id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticsDoNotExist_whenGetStatisticsByUserId_thenThrowNotFoundException() {
        when(statisticRepository.findByUserId(1L)).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.getStatisticsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No statistics found for user id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequest_whenUpdateStatisticById_thenUpdateStatistic() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticRepository.findById(1L)).thenReturn(Optional.of(statistic));
        when(statisticMapper.updateEntityWithDto(requestStatisticDto, statistic)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestStatisticDto.getExchange().toUpperCase())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(requestStatisticDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.of(1L));
        statisticService.updateStatisticById(1L, requestStatisticDto);
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    void givenBadRequest_whenUpdateStatisticById_thenThrowValidationException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        ValidationException exception = assertThrows(ValidationException.class, () -> statisticService.updateStatisticById(1L, requestStatisticDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUserDoesNotExist_whenUpdateStatisticById_thenThrowNotFoundException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticRepository.findById(1L)).thenReturn(Optional.of(statistic));
        when(statisticMapper.updateEntityWithDto(requestStatisticDto, statistic)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.updateStatisticById(1L, requestStatisticDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with username: testUser", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateStatisticById_thenThrowNotFoundException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticRepository.findById(1L)).thenReturn(Optional.of(statistic));
        when(statisticMapper.updateEntityWithDto(requestStatisticDto, statistic)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestStatisticDto.getExchange().toUpperCase())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.updateStatisticById(1L, requestStatisticDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockTickerDoesNotExist_whenUpdateStatisticById_thenThrowNotFoundException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticRepository.findById(1L)).thenReturn(Optional.of(statistic));
        when(statisticMapper.updateEntityWithDto(requestStatisticDto, statistic)).thenReturn(statistic);
        when(userRepository.findIdByUsername(requestStatisticDto.getUsername().toUpperCase())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestStatisticDto.getExchange().toUpperCase())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(requestStatisticDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.updateStatisticById(1L, requestStatisticDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker D05 cannot be found in exchange: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticDoesNotExist_whenUpdateStatisticById_thenThrowNotFoundException() {
        StatisticDto requestStatisticDto = new StatisticDto("testUser", "D05", "SGX", "546.88006546", "103002.592101", "240112.908006", "3001110.4501", "103.2204", "111.11", "2");
        Statistic statistic = new Statistic();
        statistic.setUsername("testUser");
        statistic.setStockTicker("D05");
        statistic.setExchange("SGX");
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.updateStatisticById(1L, requestStatisticDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No statistic found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticsExist_whenDeleteAllStatistics_thenDeleteStatistics() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findAll()).thenReturn(statistics);
        statisticService.deleteAllStatistics();
        verify(statisticRepository, times(1)).deleteAll();
    }

    @Test
    void givenStatisticsDoNotExist_whenDeleteAllStatistics_thenThrowNotFoundException() {
        when(statisticRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.deleteAllStatistics());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No statistic(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticExists_whenDeleteStatisticById_thenDeleteStatistic() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        when(statisticRepository.findById(statistic.getStatisticId())).thenReturn(Optional.of(statistic));
        statisticService.deleteStatisticById(statistic.getStatisticId());
        verify(statisticRepository, times(1)).deleteById(statistic.getStatisticId());
    }

    @Test
    void givenStatisticDoesNotExist_whenDeleteStatisticById_thenThrowNotFoundException() {
        when(statisticRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.deleteStatisticById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No statistic found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStatisticsExist_whenDeleteStatisticsByUserId_thenDeleteStatistics() {
        Statistic statistic = new Statistic();
        statistic.setStatisticId(1L);
        statistic.setUserId(1L);
        statistic.setStockId(1L);
        statistic.setTotalUnits("546.88006546");
        statistic.setTotalCost("103002.592101");
        statistic.setTotalValue("240112.908006");
        statistic.setRealizedProfits("3001110.4501");
        statistic.setUnrealizedProfits("103.2204");
        statistic.setDividendsEarned("111.11");
        statistic.setTotalProfits("2");
        List<Statistic> statistics = List.of(statistic);
        when(statisticRepository.findByUserId(statistic.getUserId())).thenReturn(statistics);
        statisticService.deleteStatisticsByUserId(statistic.getUserId());
        verify(statisticRepository, times(1)).deleteByUserId(statistic.getUserId());
    }

    @Test
    void givenStatisticsDoNotExist_whenDeleteStatisticsByUserId_thenThrowNotFoundException() {
        when(statisticRepository.findByUserId(1L)).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.deleteStatisticsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No statistics found for user id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionsExist_whenCalculateTotalUnitsOwnedOnGivenDate_thenReturnTotalUnits() {
        Transaction transaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> transactions = List.of(transaction1, transaction2);
        when(transactionRepository.findByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(transactions);
        when(stockRepository.findStockTickerByStockId(any())).thenReturn("D05");
        BigDecimal totalUnits = statisticService.calculateTotalUnitsOwnedOnGivenDate(any(), any(), any());
        assertEquals(new BigDecimal("14.26525"), totalUnits);
    }

    @Test
    void givenStockIdsAndStockAlreadyExists_whenCalculateTotalUnits_thenUpdateTotalUnits() {
        List<Long> stockIds = List.of(1L);
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.calculateTotalUnits(any());
        verify(statisticRepository, times(1)).updateUnits(any(), any(), any());
    }

    @Test
    void givenNoStockIds_whenCalculateTotalUnits_thenThrowNotFoundException() {
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalUnits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found for user with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockIdsAndStockDoesNotAlreadyExist_whenCalculateTotalUnits_thenSaveStatistic() {
        List<Long> stockIds = List.of(1L);
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        statisticService.calculateTotalUnits(any());
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStockIdAndDisplayCurrencyIsSameAsStockCurrency_whenCalculateTotalCostByStock_thenReturnTotalCost() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        Transaction transaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> transactions = List.of(transaction1, transaction2);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(transactions);
        BigDecimal totalCost = statisticService.calculateTotalCostByStock(1L, 1L);
        assertEquals(new BigDecimal("701.858091704"), totalCost);
    }

    @Test
    void givenUserDoesNotExist_whenCalculateTotalCostByStock_thenThrowNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalCostByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenCalculateTotalCostByStock_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalCostByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenBuyTransactionsDoNotExist_whenCalculateTotalCostByStock_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockTicker("D05");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalCostByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No buy transactions found with ticker D05 for user id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStockIdAndDisplayCurrencyIsDifferentFromStockCurrency_whenCalculateTotalCostByStock_thenReturnTotalCost() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("USD");
        Stock stock = new Stock();
        Transaction transaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> transactions = List.of(transaction1, transaction2);
        Rate rate = new Rate(1L, "SGD/USD", "0.735063");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(transactions);
        when(rateRepository.findByRateNameIgnoreCase(any())).thenReturn(Optional.of(rate));
        BigDecimal totalCost = statisticService.calculateTotalCostByStock(1L, 1L);
        assertEquals(new BigDecimal("517.483640242217352"), totalCost);
    }

    @Test
    void givenRateDoesNotExist_whenCalculateTotalCostByStock_thenReturnTotalCost() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("USD");
        Stock stock = new Stock();
        Transaction transaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> transactions = List.of(transaction1, transaction2);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(transactions);
        when(rateRepository.findByRateNameIgnoreCase(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalCostByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate found with name: SGD/USD", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExist_whenCalculateTotalCost_thenSaveTotalCost() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setStockTicker("D05");
        Transaction transaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> transactions = List.of(transaction1, transaction2);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(transactions);
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        statisticService.calculateTotalCost(1L);
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStatisticAlreadyExists_whenCalculateTotalCost_thenUpdateTotalCost() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setStockTicker("D05");
        Transaction transaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> transactions = List.of(transaction1, transaction2);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(transactions);
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        statisticService.calculateTotalCost(1L);
        verify(statisticRepository, times(1)).updateCost(any(), any(), any());
    }

    @Test
    void givenStocksIdsDoNotExist_whenCalculateTotalCost_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setStockTicker("D05");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalCost(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found for user with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStocksDoNotExist_whenCalculateTotalCost_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setStockTicker("D05");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalCost(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(NO_STOCK_FOUND_WITH_WITH_STOCK_IDS, exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenInvalidStock_whenCalculateTotalCost_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setStockTicker("D05");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.empty()));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalCost(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(INVALID_STOCK, exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStockId_whenCalculateTotalValueByStock_thenReturnTotalValue() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setStockTicker("D05");
        stock.setBaseCurrency("SGD");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal("10"));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal("40"));
        BigDecimal totalValue = statisticService.calculateTotalValueByStock(1L, 1L);
        assertEquals(new BigDecimal(400), totalValue);
    }

    @Test
    void givenInvalidStock_whenCalculateTotalValueByStock_thenReturnTotalValue() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setStockTicker("D05");
        stock.setBaseCurrency("SGD");
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalValueByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExistAndDisplayCurrencyIsSameAsStockCurrency_whenCalculateTotalValue_thenSaveTotalValue() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal(10));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal(33));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        when(statisticRepository.getValue(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        statisticService.calculateTotalValue(1L);
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExistAndDisplayCurrencyIsDifferentFromStockCurrency_whenCalculateTotalValue_thenSaveTotalValue() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("USD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        Rate rate = new Rate(1L, "SGD/USD", "0.735063");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal(10));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal(33));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        when(statisticRepository.getValue(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        when(rateRepository.findByRateNameIgnoreCase(any())).thenReturn(Optional.of(rate));
        statisticService.calculateTotalValue(1L);
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExistAndDisplayCurrencyIsDifferentFromStockCurrencyButRateDoesNotExist_whenCalculateTotalValue_thenSaveTotalValue() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("USD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal(10));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal(33));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(rateRepository.findByRateNameIgnoreCase(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalValue(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate found with name: SGD/USD", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStatisticAlreadyExists_whenCalculateTotalValue_thenUpdateTotalValue() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal(10));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal(33));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        when(statisticRepository.getValue(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        statisticService.calculateTotalValue(1L);
        verify(statisticRepository, times(1)).updateValue(any(), any(), any());
    }

    @Test
    void givenStocksIdsDoNotExist_whenCalculateTotalValue_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalValue(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found for user with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStocksDoNotExist_whenCalculateTotalValue_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalValue(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found with provided stock ids.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenInvalidStock_whenCalculateTotalValue_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of(1L));
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.empty()));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalValue(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(INVALID_STOCK, exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStockId_whenCalculateRealizedProfitsByStock_thenReturnRealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "0", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "0", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        Transaction buyTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "0", "32.7758", "3.51", "SGD");
        Transaction buyTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "0", "40.2859", "2.43", "SGD");
        List<Transaction> buyTransactions = List.of(buyTransaction1, buyTransaction2);
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(buyTransactions);
        BigDecimal totalRealizedProfits = statisticService.calculateRealizedProfitsByStock(1L, 1L);
        assertEquals(new BigDecimal("0"), totalRealizedProfits);
    }

    @Test
    void givenStockDoesNotExist_whenCalculateRealizedProfitsByStock_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateRealizedProfitsByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenNoSellTransactions_whenCalculateRealizedProfitsByStock_thenReturnZero() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(List.of());
        BigDecimal totalRealizedProfits = statisticService.calculateRealizedProfitsByStock(1L, 1L);
        assertEquals(new BigDecimal("0"), totalRealizedProfits);
    }

    @Test
    void givenNoBuyTransactions_whenCalculateRealizedProfitsByStock_thenThrowGeneralException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(List.of());
        GeneralException exception = assertThrows(GeneralException.class, () -> statisticService.calculateRealizedProfitsByStock(1L, 1L));
        assertEquals(INTERNAL_SERVER_ERROR_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("There should be a buy transaction before a sell transaction.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStockIdAndDisplayCurrencyIsDifferentFromTransactionCurrency_whenCalculateRealizedProfitsByStock_thenReturnRealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("USD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        Rate rate = new Rate(1L, "SGD/USD", "0.735063");
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        Transaction buyTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction buyTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> buyTransactions = List.of(buyTransaction1, buyTransaction2);
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(rateRepository.findByRateNameIgnoreCase(any())).thenReturn(Optional.of(rate));
        when(transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(buyTransactions);
        BigDecimal totalRealizedProfits = statisticService.calculateRealizedProfitsByStock(1L, 1L);
        assertEquals(new BigDecimal("-194.68072568178264466022"), totalRealizedProfits);
    }

    @Test
    void givenRateDoesNotExist_whenCalculateRealizedProfitsByStock_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("USD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(rateRepository.findByRateNameIgnoreCase(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateRealizedProfitsByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No rate found with name: SGD/USD", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExist_whenCalculateRealizedProfits_thenSaveRealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        Transaction buyTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction buyTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> buyTransactions = List.of(buyTransaction1, buyTransaction2);
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(statisticRepository.getRealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(buyTransactions);
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        statisticService.calculateRealizedProfits(1L);
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStatisticAlreadyExists_whenCalculateRealizedProfits_thenUpdateRealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        Transaction buyTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction buyTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> buyTransactions = List.of(buyTransaction1, buyTransaction2);
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(statisticRepository.getRealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(buyTransactions);
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.calculateRealizedProfits(1L);
        verify(statisticRepository, times(1)).updateRealizedProfits(any(), any(), any());
    }

    @Test
    void givenNoStockIds_whenCalculateRealizedProfits_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateRealizedProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found for user with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenNoStocks_whenCalculateRealizedProfits_thenSaveRealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateRealizedProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(NO_STOCK_FOUND_WITH_WITH_STOCK_IDS, exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenCalculateRealizedProfits_thenSaveRealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.empty()));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateRealizedProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Invalid stock", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStockId_whenCalculateUnRealizedProfitsByStock_thenReturnUnrealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal("40.32"));
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("789.54"));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal("37.32"));
        BigDecimal totalUnrealizedProfits = statisticService.calculateUnrealizedProfitsByStock(1L, 1L);
        assertEquals(new BigDecimal("715.20240000000001416"), totalUnrealizedProfits);
    }

    @Test
    void givenValidUserIdAndStockIdAndZeroStockUnits_whenCalculateUnRealizedProfitsByStock_thenReturnUnrealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal("40.32"));
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("789.54"));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(BigDecimal.ZERO);
        BigDecimal totalUnrealizedProfits = statisticService.calculateUnrealizedProfitsByStock(1L, 1L);
        assertEquals(new BigDecimal("0.00"), totalUnrealizedProfits);
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExist_whenCalculateUnrealizedProfits_thenSaveUnrealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal("40.32"));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal("37.32"));
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("789.54"));
        when(statisticRepository.getUnrealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        statisticService.calculateUnrealizedProfits(1L);
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStatisticAlreadyExists_whenCalculateUnrealizedProfits_thenUpdateUnrealizedProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal("40.32"));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal("37.32"));
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("789.54"));
        when(statisticRepository.getUnrealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.calculateUnrealizedProfits(1L);
        verify(statisticRepository, times(1)).updateUnrealizedProfits(any(), any(), any());
    }

    @Test
    void givenUserDoesNotExist_whenCalculateUnrealizedProfits_thenUpdateUnrealizedProfits() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateUnrealizedProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenNoStockIds_whenCalculateUnrealizedProfits_thenUpdateUnrealizedProfits() {
        User user = new User();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateUnrealizedProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found for user with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStockId_whenCalculateTotalDividendsEarnedByStock_thenReturnTotalDividends() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2024-04-05", "2024-04-19", "0.54");
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getEarliestTransactionDate(any(), any())).thenReturn("2023-04-14");
        when(dividendRepository.getRelevantDividends(any(), any())).thenReturn(List.of(dividend));
        BigDecimal totalDividends = statisticService.calculateTotalDividendsEarnedByStock(1L, 1L);
        assertEquals(new BigDecimal("0.00"), totalDividends);
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExist_whenCalculateTotalDividendsEarned_thenSaveTotalDividends() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2024-04-05", "2024-04-19", "0.54");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findDividendStocksByIds(stockIds)).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(transactionRepository.getEarliestTransactionDate(any(), any())).thenReturn("2023-04-14");
        when(dividendRepository.getRelevantDividends(any(), any())).thenReturn(List.of(dividend));
        when(statisticRepository.getDividends(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        statisticService.calculateTotalDividendsEarned(1L);
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStatisticAlreadyExists_whenCalculateTotalDividendsEarned_thenUpdateTotalDividends() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        Dividend dividend = new Dividend(1L, 1L, "D05", 1L, "SGX", "2024-04-05", "2024-04-19", "0.54");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findDividendStocksByIds(stockIds)).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(transactionRepository.getEarliestTransactionDate(any(), any())).thenReturn("2023-04-14");
        when(dividendRepository.getRelevantDividends(any(), any())).thenReturn(List.of(dividend));
        when(statisticRepository.getDividends(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.calculateTotalDividendsEarned(1L);
        verify(statisticRepository, times(1)).updateDividends(any(), any(), any());
    }

    @Test
    void givenNoStockIds_whenCalculateTotalDividendsEarned_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalDividendsEarned(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found for user with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStockId_whenCalculateTotalProfitsByStock_thenReturnTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.of(statistic));
        BigDecimal totalProfits = statisticService.calculateTotalProfitsByStock(1L, 1L);
        assertEquals(new BigDecimal("121.10"), totalProfits);
    }

    @Test
    void givenNoStatistics_whenCalculateTotalProfitsByStock_thenReturnTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalProfitsByStock(1L, 1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Error retrieving statistic for stock ID 1 and user id 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserIdAndStatisticDoesNotExist_whenCalculateTotalProfits_thenSaveTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.of(statistic));
        when(statisticRepository.getTotalProfits(any(), any())).thenReturn(new BigDecimal("888.88"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(false);
        statisticService.calculateTotalProfits(1L);
        verify(statisticRepository, times(1)).save(any());
    }

    @Test
    void givenValidUserIdAndStatisticAlreadyExists_whenCalculateTotalProfits_thenUpdateTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.of(statistic));
        when(statisticRepository.getTotalProfits(any(), any())).thenReturn(new BigDecimal("888.88"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.calculateTotalProfits(1L);
        verify(statisticRepository, times(1)).updateTotalProfits(any(), any(), any());
    }

    @Test
    void givenUserDoesNotExist_whenCalculateTotalProfits_thenThrowNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenNoStockIds_whenCalculateTotalProfits_thenThrowNotFoundException() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stocks found for user with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenNoStocks_whenCalculateTotalProfits_thenReturnTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(NO_STOCK_FOUND_WITH_WITH_STOCK_IDS, exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenNoStock_whenCalculateTotalProfits_thenReturnTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.empty()));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> statisticService.calculateTotalProfits(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Invalid stock", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidUserId_whenUpdateStatisticsForUser_thenUpdateStatistics() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        Transaction buyTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction buyTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> buyTransactions = List.of(buyTransaction1, buyTransaction2);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(statisticRepository.getRealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(buyTransactions);
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(buyTransactions);
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal("10"));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal("40"));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(statisticRepository.getValue(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        when(statisticRepository.getUnrealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.getDividends(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.of(statistic));
        when(statisticRepository.getTotalProfits(any(), any())).thenReturn(new BigDecimal("888.88"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.updateStatisticsForUser(1L);
        verify(statisticRepository, times(6)).existsByUserIdAndStockId(any(), any());
    }

    @Test
    void givenValidUsers_whenUpdateStatisticsForAllUsers_thenUpdateStatistics() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> userIds = List.of(1L);
        List<Long> stockIds = List.of(1L);
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        Transaction sellTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Sell", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction sellTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Sell", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> sellTransactions = List.of(sellTransaction1, sellTransaction2);
        Transaction buyTransaction1 = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction buyTransaction2 = new Transaction(2L, 1L, "testUser", "2023-06-17", "Buy", 1L, "D05", "SGX", "3.12562", "40.2859", "2.43", "SGD");
        List<Transaction> buyTransactions = List.of(buyTransaction1, buyTransaction2);
        when(userRepository.findAllUserIds()).thenReturn(userIds);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(transactionRepository.getSellTransactionsByStock(any(), any())).thenReturn(sellTransactions);
        when(statisticRepository.getRealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(any(), any(), any())).thenReturn(buyTransactions);
        when(transactionRepository.getBuyTransactionsByStock(any(), any())).thenReturn(buyTransactions);
        when(statisticRepository.getCost(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        when(statisticRepository.getStockUnits(any(), any())).thenReturn(new BigDecimal("10"));
        when(stockRepository.findLastPriceByStockId(any())).thenReturn(new BigDecimal("40"));
        when(stockRepository.findBaseCurrencyByStockId(any())).thenReturn("SGD");
        when(statisticRepository.getValue(any(), any())).thenReturn(new BigDecimal("517.483640242217352"));
        when(statisticRepository.getUnrealizedProfits(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.getDividends(any(), any())).thenReturn(new BigDecimal("430.42"));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.of(statistic));
        when(statisticRepository.getTotalProfits(any(), any())).thenReturn(new BigDecimal("888.88"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.updateStatisticsForAllUsers();
        verify(statisticRepository, times(6)).existsByUserIdAndStockId(any(), any());
    }

    @Test
    void givenValidUserId_whenUpdateTotalProfitsForUser_thenUpdateTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> stockIds = List.of(1L);
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.of(statistic));
        when(statisticRepository.getTotalProfits(any(), any())).thenReturn(new BigDecimal("888.88"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.updateTotalProfitsForUser(1L);
        verify(statisticRepository, times(1)).existsByUserIdAndStockId(any(), any());
    }

    @Test
    void givenValidUsers_whenUpdateTotalProfitsForAllUsers_thenUpdateTotalProfits() {
        User user = new User();
        user.setUserId(1L);
        user.setDisplayCurrency("SGD");
        Stock stock = new Stock();
        stock.setStockId(1L);
        stock.setBaseCurrency("SGD");
        List<Long> userIds = List.of(1L);
        List<Long> stockIds = List.of(1L);
        Statistic statistic = new Statistic(1L, 1L, "testUser", 1L, "D05", "SGX", "2849.99", "96.66", "25.74", "11.44", "42.01", "67.65", "90.65");
        when(userRepository.findAllUserIds()).thenReturn(userIds);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findAllStockIdsByUserId(any())).thenReturn(stockIds);
        when(stockRepository.findByStockIds(any())).thenReturn(List.of(Optional.of(stock)));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(statisticRepository.findByUserIdAndStockId(any(), any())).thenReturn(Optional.of(statistic));
        when(statisticRepository.getTotalProfits(any(), any())).thenReturn(new BigDecimal("888.88"));
        when(statisticRepository.existsByUserIdAndStockId(any(), any())).thenReturn(true);
        statisticService.updateTotalProfitsForAllUsers();
        verify(statisticRepository, times(1)).existsByUserIdAndStockId(any(), any());
    }
}