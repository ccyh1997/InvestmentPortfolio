package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.TransactionDto;
import com.example.investmentportfolio.mapper.TransactionMapper;
import com.example.investmentportfolio.model.Stock;
import com.example.investmentportfolio.model.Transaction;
import com.example.investmentportfolio.model.User;
import com.example.investmentportfolio.repository.ExchangeRepository;
import com.example.investmentportfolio.repository.StockRepository;
import com.example.investmentportfolio.repository.TransactionRepository;
import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.service.TransactionService;
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
public class TransactionServiceImpl implements TransactionService {
    private static final Logger LOGGER = LogManager.getLogger(TransactionServiceImpl.class);
    public static final String NO_TRANSACTION_FOUND_WITH_ID = "No transaction found with id: %d";
    private final TransactionRepository transactionRepository;
    private final StockRepository stockRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final Validator validator;

    public TransactionServiceImpl(TransactionRepository transactionRepository, StockRepository stockRepository, ExchangeRepository exchangeRepository, UserRepository userRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.stockRepository = stockRepository;
        this.exchangeRepository = exchangeRepository;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Set<ConstraintViolation<TransactionDto>> violations = validator.validate(transactionDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        }
        else {
            Transaction transaction = transactionMapper.convertToEntity(transactionDto);
            Optional<Long> optionalUserId = userRepository.findIdByUsername(transactionDto.getUsername().toUpperCase());
            if (optionalUserId.isPresent()) {
                transaction.setUserId(optionalUserId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No user found with username: %s", transactionDto.getUsername()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(transactionDto.getExchange().toUpperCase());
            if (optionalExchangeId.isEmpty()) {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", transactionDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(transactionDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                transaction.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", transactionDto.getStockTicker(), transactionDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            transactionRepository.save(transaction);
            return transactionMapper.convertToDto(transaction);
        }
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (!transactions.isEmpty()) {
            return transactions.stream()
                    .map(transaction -> {
                        Optional<User> optionalUser = userRepository.findById(transaction.getUserId());
                        optionalUser.ifPresent(user -> transaction.setUsername(String.valueOf(user.getUsername())));
                        Optional<Stock> optionalStock = stockRepository.findById(transaction.getStockId());
                        optionalStock.ifPresent(stock -> transaction.setStockTicker(stock.getStockTicker()));
                        Optional<String> optionalExchange = stockRepository.findExchangeByStockId(transaction.getStockId());
                        optionalExchange.ifPresent(transaction::setExchange);
                        return transactionMapper.convertToDto(transaction);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No transaction(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public TransactionDto getTransactionById(Long transactionId) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            Optional<User> optionalUser = userRepository.findById(transaction.getUserId());
            optionalUser.ifPresent(user -> transaction.setUsername(String.valueOf(user.getUsername())));
            Optional<Stock> optionalStock = stockRepository.findById(transaction.getStockId());
            optionalStock.ifPresent(stock -> transaction.setStockTicker(stock.getStockTicker()));
            Optional<String> optionalExchange = stockRepository.findExchangeByStockId(transaction.getStockId());
            optionalExchange.ifPresent(transaction::setExchange);
            return transactionMapper.convertToDto(transaction);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_TRANSACTION_FOUND_WITH_ID, transactionId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<TransactionDto> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        if (!transactions.isEmpty()) {
            return transactions.stream()
                    .map(transaction -> {
                        Optional<User> optionalUser = userRepository.findById(transaction.getUserId());
                        optionalUser.ifPresent(user -> transaction.setUsername(String.valueOf(user.getUsername())));
                        Optional<Stock> optionalStock = stockRepository.findById(transaction.getStockId());
                        optionalStock.ifPresent(stock -> transaction.setStockTicker(stock.getStockTicker()));
                        Optional<String> optionalExchange = stockRepository.findExchangeByStockId(transaction.getStockId());
                        optionalExchange.ifPresent(transaction::setExchange);
                        return transactionMapper.convertToDto(transaction);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No transactions found for user id: %d", userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public TransactionDto updateTransactionById(Long transactionId, TransactionDto transactionDto) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalTransaction.isPresent()) {
            Transaction updatedTransaction = transactionMapper.updateEntityWithDto(transactionDto, optionalTransaction.get());
            Optional<Long> optionalUserId = userRepository.findIdByUsername(transactionDto.getUsername().toUpperCase());
            if (optionalUserId.isPresent()) {
                updatedTransaction.setUserId(optionalUserId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No user found with username: %s", transactionDto.getUsername()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(transactionDto.getExchange().toUpperCase());
            if (optionalExchangeId.isEmpty()) {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", transactionDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(transactionDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                updatedTransaction.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", transactionDto.getStockTicker(), transactionDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            transactionRepository.save(updatedTransaction);
            return transactionMapper.convertToDto(updatedTransaction);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_TRANSACTION_FOUND_WITH_ID, transactionId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (!transactions.isEmpty()) {
            transactionRepository.deleteAll();
        } else {
            List<String> errorMessages = Collections.singletonList("No transaction(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteTransactionById(Long transactionId) {
        Optional<Transaction> optionalDividend = transactionRepository.findById(transactionId);
        if (optionalDividend.isPresent()) {
            transactionRepository.deleteById(transactionId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_TRANSACTION_FOUND_WITH_ID, transactionId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }
}
