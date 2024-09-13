package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    TransactionDto createTransaction(TransactionDto transactionDto);
    List<TransactionDto> getAllTransactions();
    TransactionDto getTransactionById(Long transactionId);
    List<TransactionDto> getTransactionsByUserId(Long userId);
    TransactionDto updateTransactionById(Long transactionId, TransactionDto transactionDto);
    void deleteAllTransactions();
    void deleteTransactionById(Long transactionId);
}
