package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.TransactionDto;
import com.example.investmentportfolio.model.Transaction;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionMapperTest {
    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void givenTransaction_whenConvertToDto_thenReturnTransactionDto() {
        Transaction transaction = new Transaction(1L, 1L, "testUser", "2023-04-14", null, 1L, null, "SGX", "17.39087", "32.7758", "3", "SGD");
        TransactionDto transactionDto = transactionMapper.convertToDto(transaction);
        assertEquals("testUser", transactionDto.getUsername());
        assertEquals("2023-04-14", transactionDto.getTransactionDate());
        assertNull(transactionDto.getTransactionType());
        assertNull(transactionDto.getStockTicker());
        assertEquals("SGX", transactionDto.getExchange());
        assertEquals("17.39087", transactionDto.getUnits());
        assertEquals("32.7758", transactionDto.getUnitPrice());
        assertEquals("3", transactionDto.getFees());
        assertEquals("SGD", transactionDto.getCurrency());
    }

    @Test
    void givenNullTransaction_whenConvertToDto_thenReturnNull() {
        TransactionDto transactionDto = transactionMapper.convertToDto(null);
        assertNull(transactionDto);
    }

    @Test
    void givenTransactionDto_whenConvertToEntity_thenReturnTransaction() {
        TransactionDto transactionDto = new TransactionDto("testUser", "2023-04-14", "", "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction = transactionMapper.convertToEntity(transactionDto);
        assertEquals("testUser", transaction.getUsername());
        assertEquals("2023-04-14", transaction.getTransactionDate());
        assertEquals("", transaction.getTransactionType());
        assertEquals("D05", transaction.getStockTicker());
        assertEquals("SGX", transaction.getExchange());
        assertEquals("17.39087", transaction.getUnits());
        assertEquals("32.7758", transaction.getUnitPrice());
        assertEquals("3.51", transaction.getFees());
        assertEquals("SGD", transaction.getCurrency());
    }

    @Test
    void givenNullTransactionDto_whenConvertToEntity_thenReturnNull() {
        Transaction transaction = transactionMapper.convertToEntity(null);
        assertNull(transaction);
    }

    @Test
    void givenTransactionDto_whenUpdateEntityWithDto_thenReturnTransaction() {
        TransactionDto transactionDto = new TransactionDto("testUser", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "3.51", "SGD");
        Transaction transaction = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        transactionMapper.updateEntityWithDto(transactionDto, transaction);
        assertEquals("testUser", transaction.getUsername());
        assertEquals("2023-04-14", transaction.getTransactionDate());
        assertEquals("Buy", transaction.getTransactionType());
        assertEquals("D05", transaction.getStockTicker());
        assertEquals("SGX", transaction.getExchange());
        assertEquals("17.39087", transaction.getUnits());
        assertEquals("32.7758", transaction.getUnitPrice());
        assertEquals("3.51", transaction.getFees());
        assertEquals("SGD", transaction.getCurrency());
    }

    @Test
    void givenNullTransactionDto_whenUpdateEntityWithDto_thenReturnNull() {
        Transaction transaction = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        Transaction updatedTransaction = transactionMapper.updateEntityWithDto(null, transaction);
        assertEquals(transaction, updatedTransaction);
    }

    @Test
    void givenNullTransactionDtoFields_whenUpdateEntityWithDto_thenSkipUpdate() {
        Transaction transaction = new Transaction(1L, 1L, "testUser", "2023-04-14", "Buy", 1L, "D05", "SGX", "17.39087", "32.7758", "SGD", "3.51");
        TransactionDto transactionDto = new TransactionDto(null, null, null, null, null, null, null, null, null);
        Transaction updatedTransaction = transactionMapper.updateEntityWithDto(transactionDto, transaction);
        assertEquals(transaction, updatedTransaction);
    }
}