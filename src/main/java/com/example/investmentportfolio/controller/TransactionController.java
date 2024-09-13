package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.TransactionDto;
import com.example.investmentportfolio.service.TransactionService;
import com.example.investmentportfolio.util.CustomError;
import com.example.investmentportfolio.util.ErrorConstants;
import com.example.investmentportfolio.util.ValidationException;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private static final Logger LOGGER = LogManager.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            TransactionDto createdTransactionDto = transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransactionDto);
        }
    }

    // READ
    @GetMapping("/all")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        List<TransactionDto> transactionDtoList = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactionDtoList);
    }

    @GetMapping("/id/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long transactionId) {
        TransactionDto transactionDto = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transactionDto);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionDto> transactionDtoList = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactionDtoList);
    }

    // UPDATE
    @PostMapping("/update/id/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransactionById(@PathVariable Long transactionId, @Valid @RequestBody TransactionDto transactionDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            TransactionDto updatedTransactionDto = transactionService.updateTransactionById(transactionId, transactionDto);
            return ResponseEntity.ok(updatedTransactionDto);
        }
    }

    // DELETE
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllTransactions() {
        transactionService.deleteAllTransactions();
        return ResponseEntity.ok("Successfully deleted all transactions.");
    }

    @DeleteMapping("/delete/id/{transactionId}")
    public ResponseEntity<String> deleteTransactionById(@PathVariable Long transactionId) {
        transactionService.deleteTransactionById(transactionId);
        return ResponseEntity.ok(String.format("Successfully deleted transaction with id: %d", transactionId));
    }
}
