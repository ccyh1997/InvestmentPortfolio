package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.TransactionDto;
import com.example.investmentportfolio.service.TransactionService;
import com.example.investmentportfolio.util.Constants;
import com.example.investmentportfolio.util.CustomError;
import com.example.investmentportfolio.util.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction Controller", description = "Provides endpoints for managing exchange transactions.")
public class TransactionController {
    private static final Logger LOGGER = LogManager.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Create a new transaction",
            description = "Add a new transaction to the portfolio. Users with 'ADMIN' or 'USER' roles can perform this operation.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TransactionDto.class,
                                    example = """
                                        {
                                            "username": "ccyh_97",
                                            "transactionDate": "2024-01-01",
                                            "transactionType": "buy",
                                            "stockTicker": "d05",
                                            "exchange": "sgx",
                                            "units": "100",
                                            "unitPrice": "33.90900",
                                            "fees": "1.45",
                                            "currency": "sgd"
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Transaction successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = TransactionDto.class,
                                            example = """
                                                {
                                                    "username": "ccyh_97",
                                                    "transactionDate": "2024-01-01",
                                                    "transactionType": "Buy",
                                                    "stockTicker": "D05",
                                                    "exchange": "SGX",
                                                    "units": "100",
                                                    "unitPrice": "33.909",
                                                    "fees": "1.45",
                                                    "currency": "SGD"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/create")
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            TransactionDto createdTransactionDto = transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransactionDto);
        }
    }

    @Operation(
            summary = "Retrieve all transactions",
            description = "Fetch a list of all transactions in the portfolio. Users with 'ADMIN' or 'USER' roles can perform this operation.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all transactions",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = TransactionDto.class,
                                            example = """
                                                [
                                                    {
                                                        "username": "ccyh_97",
                                                        "transactionDate": "2023-04-14",
                                                        "transactionType": "Buy",
                                                        "stockTicker": "D05",
                                                        "exchange": "SGX",
                                                        "units": "17.39087",
                                                        "unitPrice": "32.7758",
                                                        "fees": "0.",
                                                        "currency": "SGD"
                                                    },
                                                    {
                                                        "username": "ccyh_97",
                                                        "transactionDate": "2023-05-12",
                                                        "transactionType": "Buy",
                                                        "stockTicker": "D05",
                                                        "exchange": "SGX",
                                                        "units": "18.53791",
                                                        "unitPrice": "30.7478",
                                                        "fees": "0.",
                                                        "currency": "SGD"
                                                    },
                                                    {
                                                        "username": "ccyh_97",
                                                        "transactionDate": "2023-05-25",
                                                        "transactionType": "Buy",
                                                        "stockTicker": "CSPX",
                                                        "exchange": "LSE",
                                                        "units": "2",
                                                        "unitPrice": "582.1851",
                                                        "fees": "5.72",
                                                        "currency": "SGD"
                                                    }
                                                ]
                                                """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/all")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        List<TransactionDto> transactionDtoList = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactionDtoList);
    }

    @Operation(
            summary = "Retrieve a transaction by ID",
            description = "Fetch a specific transaction from the portfolio using its ID. Users with 'ADMIN' or 'USER' roles can perform this operation.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved transaction by ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = TransactionDto.class,
                                            example = """
                                                {
                                                    "username": "ccyh_97",
                                                    "transactionDate": "2023-04-14",
                                                    "transactionType": "Buy",
                                                    "stockTicker": "D05",
                                                    "exchange": "SGX",
                                                    "units": "17.39087",
                                                    "unitPrice": "32.7758",
                                                    "fees": "0.",
                                                    "currency": "SGD"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/id/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long transactionId) {
        TransactionDto transactionDto = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transactionDto);
    }

    @Operation(
            summary = "Retrieve stock details by user ID",
            description = "Fetch stock details for a specific user by their ID. Users with 'ADMIN' or 'USER' roles can perform this operation.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved stock details by user ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = TransactionDto.class,
                                            example = """
                                                {
                                                    "stockTicker": "OV8",
                                                    "stockName": "Sheng Siong Group Ltd",
                                                    "stockType": "Equity",
                                                    "exchange": "SGX",
                                                    "lastPrice": "1.5306078",
                                                    "baseCurrency": "SGD",
                                                    "divInd": "Y",
                                                    "delistInd": "N"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionDto> transactionDtoList = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactionDtoList);
    }

    @Operation(
            summary = "Update a transaction by ID",
            description = "Update the details of an existing transaction using its ID. Users with 'ADMIN' or 'USER' roles can perform this operation.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TransactionDto.class,
                                    example = """
                                        {
                                            "username": "ccyh_97",
                                            "transactionDate": "2024-03-01",
                                            "transactionType": "buy",
                                            "stockTicker": "d05",
                                            "exchange": "sgx",
                                            "units": "133",
                                            "unitPrice": "36.08",
                                            "fees": "1.66",
                                            "currency": "sgd"
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated transaction",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = TransactionDto.class,
                                            example = """
                                                {
                                                    "username": "ccyh_97",
                                                    "transactionDate": "2024-03-01",
                                                    "transactionType": "Buy",
                                                    "stockTicker": "D05",
                                                    "exchange": "SGX",
                                                    "units": "133",
                                                    "unitPrice": "36.08",
                                                    "fees": "1.66",
                                                    "currency": "SGD"
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/update/id/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransactionById(@PathVariable Long transactionId, @Valid @RequestBody TransactionDto transactionDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            TransactionDto updatedTransactionDto = transactionService.updateTransactionById(transactionId, transactionDto);
            return ResponseEntity.ok(updatedTransactionDto);
        }
    }

    @Operation(
            summary = "Delete all transactions",
            description = "Remove all transactions from the portfolio. Only users with the 'ADMIN' role can perform this operation.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted all transactions",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted all transactions"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllTransactions() {
        transactionService.deleteAllTransactions();
        return ResponseEntity.ok("Successfully deleted all transactions.");
    }

    @Operation(
            summary = "Delete a transaction by ID",
            description = "Remove a specific transaction using its ID. Only users with the 'ADMIN' role can perform this operation.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted transaction by ID",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted transaction with id: 1"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/delete/id/{transactionId}")
    public ResponseEntity<String> deleteTransactionById(@PathVariable Long transactionId) {
        transactionService.deleteTransactionById(transactionId);
        return ResponseEntity.ok(String.format("Successfully deleted transaction with id: %d", transactionId));
    }
}