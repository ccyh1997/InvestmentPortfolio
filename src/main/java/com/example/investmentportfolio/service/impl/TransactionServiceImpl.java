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
public class TransactionServiceImpl implements TransactionService {
    private static final Logger LOGGER = LogManager.getLogger(TransactionServiceImpl.class);
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
        validateRequestDto(transactionDto);
        Transaction transaction = transactionMapper.convertToEntity(transactionDto);
        Optional<Long> optionalUserId = userRepository.findIdByUsername(transactionDto.getUsername().toUpperCase());
        if (optionalUserId.isPresent()) {
            transaction.setUserId(optionalUserId.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_USERNAME, transactionDto.getUsername());
        }
        Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(transactionDto.getExchange().toUpperCase());
        if (optionalExchangeId.isEmpty()) {
            throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_NAME, transactionDto.getExchange());
        }
        Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(transactionDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
        if (optionalStockId.isPresent()) {
            transaction.setStockId(optionalStockId.get());
        } else {
            throw returnNotFoundException(LOGGER, STOCK_TICKER_NOT_FOUND_IN_EXCHANGE, transactionDto.getStockTicker(), transactionDto.getExchange());
        }
        transactionRepository.save(transaction);
        return transactionMapper.convertToDto(transaction);
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (!transactions.isEmpty()) {
            return transactions.stream().map(transaction -> {
                Optional<User> optionalUser = userRepository.findById(transaction.getUserId());
                if (optionalUser.isPresent()) {
                    transaction.setUsername(optionalUser.get().getUsername());
                } else {
                    throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_ID, transaction.getUserId());
                }
                Optional<Stock> optionalStock = stockRepository.findById(transaction.getStockId());
                if (optionalStock.isPresent()) {
                    transaction.setStockTicker(optionalStock.get().getStockTicker());
                } else {
                    throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, transaction.getStockId());
                }
                Optional<String> optionalExchange = stockRepository.findExchangeByStockId(transaction.getStockId());
                if (optionalExchange.isPresent()) {
                    transaction.setExchange(optionalExchange.get());
                } else {
                    throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_STOCK_ID, transaction.getStockId());
                }
                return transactionMapper.convertToDto(transaction);
            }).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_TRANSACTIONS_FOUND);
        }
    }

    @Override
    public TransactionDto getTransactionById(Long transactionId) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            Optional<User> optionalUser = userRepository.findById(transaction.getUserId());
            if (optionalUser.isPresent()) {
                transaction.setUsername(optionalUser.get().getUsername());
            } else {
                throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_ID, transaction.getUserId());
            }
            Optional<Stock> optionalStock = stockRepository.findById(transaction.getStockId());
            if (optionalStock.isPresent()) {
                transaction.setStockTicker(optionalStock.get().getStockTicker());
            } else {
                throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, transaction.getStockId());
            }
            Optional<String> optionalExchange = stockRepository.findExchangeByStockId(transaction.getStockId());
            if (optionalExchange.isPresent()) {
                transaction.setExchange(optionalExchange.get());
            } else {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_STOCK_ID, transaction.getStockId());
            }
            return transactionMapper.convertToDto(transaction);
        } else {
            throw returnNotFoundException(LOGGER, NO_TRANSACTION_FOUND_WITH_ID, transactionId);
        }
    }

    @Override
    public List<TransactionDto> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        if (!transactions.isEmpty()) {
            return transactions.stream().map(transaction -> {
                Optional<User> optionalUser = userRepository.findById(transaction.getUserId());
                if (optionalUser.isPresent()) {
                    transaction.setUsername(optionalUser.get().getUsername());
                } else {
                    throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_ID, transaction.getUserId());
                }
                Optional<Stock> optionalStock = stockRepository.findById(transaction.getStockId());
                if (optionalStock.isPresent()) {
                    transaction.setStockTicker(optionalStock.get().getStockTicker());
                } else {
                    throw returnNotFoundException(LOGGER, NO_STOCK_FOUND_WITH_ID, transaction.getStockId());
                }
                Optional<String> optionalExchange = stockRepository.findExchangeByStockId(transaction.getStockId());
                if (optionalExchange.isPresent()) {
                    transaction.setExchange(optionalExchange.get());
                } else {
                    throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_STOCK_ID, transaction.getStockId());
                }
                return transactionMapper.convertToDto(transaction);
            }).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_TRANSACTION_FOUND_FOR_USER_WITH_ID, userId);
        }
    }

    @Override
    public TransactionDto updateTransactionById(Long transactionId, TransactionDto transactionDto) {
        validateRequestDto(transactionDto);
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalTransaction.isPresent()) {
            Transaction updatedTransaction = transactionMapper.updateEntityWithDto(transactionDto, optionalTransaction.get());
            Optional<Long> optionalUserId = userRepository.findIdByUsername(transactionDto.getUsername().toUpperCase());
            if (optionalUserId.isPresent()) {
                updatedTransaction.setUserId(optionalUserId.get());
            } else {
                throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_USERNAME, transactionDto.getUsername());
            }
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(transactionDto.getExchange().toUpperCase());
            if (optionalExchangeId.isEmpty()) {
                throw returnNotFoundException(LOGGER, NO_EXCHANGE_FOUND_WITH_NAME, transactionDto.getExchange());
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(transactionDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                updatedTransaction.setStockId(optionalStockId.get());
            } else {
                throw returnNotFoundException(LOGGER, STOCK_TICKER_NOT_FOUND_IN_EXCHANGE, transactionDto.getStockTicker(), transactionDto.getExchange());
            }
            transactionRepository.save(updatedTransaction);
            return transactionMapper.convertToDto(updatedTransaction);
        } else {
            throw returnNotFoundException(LOGGER, NO_TRANSACTION_FOUND_WITH_ID, transactionId);
        }
    }

    @Override
    @Transactional
    public void deleteAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (!transactions.isEmpty()) {
            transactionRepository.deleteAll();
        } else {
            throw returnNotFoundException(LOGGER, NO_TRANSACTIONS_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteTransactionById(Long transactionId) {
        Optional<Transaction> optionalDividend = transactionRepository.findById(transactionId);
        if (optionalDividend.isPresent()) {
            transactionRepository.deleteById(transactionId);
        } else {
            throw returnNotFoundException(LOGGER, String.format(NO_TRANSACTION_FOUND_WITH_ID, transactionId));
        }
    }

    private void validateRequestDto(TransactionDto transactionDto) {
        Set<ConstraintViolation<TransactionDto>> violations = validator.validate(transactionDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            throw returnValidationException(LOGGER, violations);
        }
    }
}
