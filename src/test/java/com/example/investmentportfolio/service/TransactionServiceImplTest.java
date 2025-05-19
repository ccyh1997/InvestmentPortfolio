package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.TransactionDto;
import com.example.investmentportfolio.mapper.TransactionMapper;
import com.example.investmentportfolio.model.Stock;
import com.example.investmentportfolio.model.Transaction;
import com.example.investmentportfolio.model.User;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.repository.StockRepository;
import com.example.investmentportfolio.repository.TransactionRepository;
import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.service.impl.TransactionServiceImpl;
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
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void givenValidRequest_whenCreateTransaction_thenCreateTransaction() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionMapper.convertToEntity(requestTransactionDto)).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestTransactionDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(requestTransactionDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.of(1L));
        transactionService.createTransaction(requestTransactionDto);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void givenBadRequest_whenCreateTransaction_thenThrowValidationException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "", "17.39087", "32.7758", "SGD", "3.51");
        ValidationException exception = assertThrows(ValidationException.class, () -> transactionService.createTransaction(requestTransactionDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUserDoesNotExist_whenCreateTransaction_thenThrowNotFoundException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionMapper.convertToEntity(requestTransactionDto)).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.createTransaction(requestTransactionDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with username: testUser", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenCreateTransaction_thenThrowNotFoundException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionMapper.convertToEntity(requestTransactionDto)).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestTransactionDto.getExchange())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.createTransaction(requestTransactionDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenCreateTransaction_thenThrowNotFoundException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionMapper.convertToEntity(requestTransactionDto)).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(requestTransactionDto.getExchange())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(requestTransactionDto.getStockTicker().toUpperCase(), 1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.createTransaction(requestTransactionDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker D05 cannot be found in exchange: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionsExist_whenGetAllTransactions_thenReturnTransactions() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        User user = new User();
        Stock stock = new Stock();
        when(transactionRepository.findAll()).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(any())).thenReturn(Optional.of("SGX"));
        transactionService.getAllTransactions();
        verify(transactionMapper, times(1)).convertToDto(any());
    }

    @Test
    void givenUserDoesNotExist_whenGetAllTransactions_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findAll()).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getAllTransactions());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenGetAllTransactions_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        User user = new User();
        when(transactionRepository.findAll()).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getAllTransactions());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetAllTransactions_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        User user = new User();
        Stock stock = new Stock();
        when(transactionRepository.findAll()).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getAllTransactions());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with stock id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionsDoNotExist_whenGetAllTransactions_thenThrowNotFoundException() {
        when(transactionRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getAllTransactions());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No transaction(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionExists_whenGetTransactionById_thenReturnTransaction() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        User user = new User();
        Stock stock = new Stock();
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(any())).thenReturn(Optional.of("SGX"));
        transactionService.getTransactionById(any());
        verify(transactionMapper, times(1)).convertToDto(any());
    }

    @Test
    void givenUserDoesNotExist_whenGetTransactionById_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenGetTransactionById_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        User user = new User();
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetTransactionById_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        User user = new User();
        Stock stock = new Stock();
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with stock id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionDoesNotExist_whenGetTransactionById_thenThrowNotFoundException() {
        when(transactionRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No transaction found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionsExist_whenGetTransactionsByUserId_thenReturnTransactions() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        User user = new User();
        Stock stock = new Stock();
        when(transactionRepository.findByUserId(any())).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(any())).thenReturn(Optional.of("SGX"));
        transactionService.getTransactionsByUserId(any());
        verify(transactionMapper, times(1)).convertToDto(any());
    }

    @Test
    void givenUserDoesNotExist_whenGetTransactionsByUserId_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findByUserId(any())).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenGetTransactionsByUserId_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        User user = new User();
        when(transactionRepository.findByUserId(any())).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No stock found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenGetTransactionsByUserId_thenThrowNotFoundException() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        User user = new User();
        Stock stock = new Stock();
        when(transactionRepository.findByUserId(any())).thenReturn(transactions);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(stockRepository.findById(any())).thenReturn(Optional.of(stock));
        when(stockRepository.findExchangeByStockId(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with stock id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionsDoNotExist_whenGetTransactionsByUserId_thenThrowNotFoundException() {
        when(transactionRepository.findByUserId(any())).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.getTransactionsByUserId(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No transactions found for user id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequestAndTransactionExists_whenUpdateTransactionById_thenUpdateTransaction() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(transactionMapper.updateEntityWithDto(any(), any())).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(any())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(any(), any())).thenReturn(Optional.of(1L));
        transactionService.updateTransactionById(any(), requestTransactionDto);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void givenBadRequest_whenUpdateTransactionById_thenUpdateTransaction() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "", "17.39087", "32.7758", "SGD", "3.51");
        ValidationException exception = assertThrows(ValidationException.class, () -> transactionService.updateTransactionById(1L, requestTransactionDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Exchange name cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUserDoesNotExist_whenUpdateTransactionById_thenThrowNotFoundException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(transactionMapper.updateEntityWithDto(any(), any())).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.updateTransactionById(1L, requestTransactionDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with username: testUser", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenExchangeDoesNotExist_whenUpdateTransactionById_thenThrowNotFoundException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(transactionMapper.updateEntityWithDto(any(), any())).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.updateTransactionById(1L, requestTransactionDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No exchange found with name: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenStockDoesNotExist_whenUpdateTransactionById_thenThrowNotFoundException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(transactionMapper.updateEntityWithDto(any(), any())).thenReturn(transaction);
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(exchangeRepository.findIdByExchange(any())).thenReturn(Optional.of(1L));
        when(stockRepository.findIdByTickerAndExchangeId(any(), any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.updateTransactionById(1L, requestTransactionDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Stock ticker D05 cannot be found in exchange: SGX", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionDoesNotExist_whenUpdateTransactionById_thenThrowNotFoundException() {
        TransactionDto requestTransactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.updateTransactionById(1L, requestTransactionDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No transaction found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionsExist_whenDeleteAllTransactions_thenDeleteTransactions() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findAll()).thenReturn(transactions);
        transactionService.deleteAllTransactions();
        verify(transactionRepository, times(1)).deleteAll();
    }

    @Test
    void givenTransactionsDoNotExist_whenDeleteAllTransactions_thenThrowNotFoundException() {
        when(transactionRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.deleteAllTransactions());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No transaction(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenTransactionExists_whenDeleteTransactionById_thenDeleteTransaction() {
        Transaction transaction = new Transaction(1L, 1L, null, "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        transactionService.deleteTransactionById(any());
        verify(transactionRepository, times(1)).deleteById(any());
    }

    @Test
    void givenTransactionDoesNotExist_whenDeleteTransactionById_thenThrowNotFoundException() {
        when(transactionRepository.findById((any()))).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> transactionService.deleteTransactionById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No transaction found with id: 1", exception.getError().getErrorMessages().getFirst());
    }
}
