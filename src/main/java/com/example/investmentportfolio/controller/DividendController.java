package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.DividendDto;
import com.example.investmentportfolio.service.DividendService;
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
@RequestMapping("/dividends")
@Tag(name = "Dividend Controller", description = "Provides endpoints for dividend management.")
public class DividendController {
    private static final Logger LOGGER = LogManager.getLogger(DividendController.class);
    private final DividendService dividendService;

    public DividendController(DividendService dividendService) {
        this.dividendService = dividendService;
    }

    @Operation(
            summary = "Create a new dividend",
            description = "Creates a new dividend record with the provided details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = DividendDto.class,
                                    example = "{ \"stockTicker\": \"d05\", \"exchange\": \"sgx\", \"exDate\": \"2023-08-17\", \"payDate\": \"2023-08-30\", \"payout\": \"0.0305000\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Dividend created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DividendDto.class,
                                            example = "{ \"stockTicker\": \"D05\", \"exchange\": \"SGX\", \"exDate\": \"2023-08-17\", \"payDate\": \"2023-08-30\", \"payout\": \"0.0305\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<DividendDto> createDividend(@Valid @RequestBody DividendDto dividendDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            DividendDto createdDividendDto = dividendService.createDividend(dividendDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDividendDto);
        }
    }

    @Operation(
            summary = "Get all dividends",
            description = "Retrieves a list of all dividends.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all dividends",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DividendDto.class,
                                            example = "[{ \"stockTicker\": \"D05\", \"exchange\": \"SGX\", \"exDate\": \"2024-04-05\", \"payDate\": \"2024-04-19\", \"payout\": \"0.54\" }, { \"stockTicker\": \"D05\", \"exchange\": \"SGX\", \"exDate\": \"2023-11-14\", \"payDate\": \"2023-11-27\", \"payout\": \"0.48\" }]"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<DividendDto>> getAllDividends() {
        List<DividendDto> dividendDtoList = dividendService.getAllDividends();
        return ResponseEntity.ok(dividendDtoList);
    }

    @Operation(
            summary = "Get dividend by ID",
            description = "Retrieves a dividend by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved dividend",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DividendDto.class,
                                            example = "{ \"stockTicker\": \"D05\", \"exchange\": \"SGX\", \"exDate\": \"2024-04-05\", \"payDate\": \"2024-04-19\", \"payout\": \"0.54\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id/{dividendId}")
    public ResponseEntity<DividendDto> getDividendById(@PathVariable Long dividendId) {
        DividendDto dividendDto = dividendService.getDividendById(dividendId);
        return ResponseEntity.ok(dividendDto);
    }

    @Operation(
            summary = "Get dividends by stock ID",
            description = "Retrieves a list of dividends by stock ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved dividends for stock",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DividendDto.class,
                                            example = "[{ \"stockTicker\": \"D05\", \"exchange\": \"SGX\", \"exDate\": \"2024-04-05\", \"payDate\": \"2024-04-19\", \"payout\": \"0.54\" }, { \"stockTicker\": \"D05\", \"exchange\": \"SGX\", \"exDate\": \"2023-11-14\", \"payDate\": \"2023-11-27\", \"payout\": \"0.48\" }]"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stockId/{stockId}")
    public ResponseEntity<List<DividendDto>> getDividendsByStockId(@PathVariable Long stockId) {
        List<DividendDto> dividendDtoList = dividendService.getDividendsByStockId(stockId);
        return ResponseEntity.ok(dividendDtoList);
    }

    @Operation(
            summary = "Get dividends by exchange ID",
            description = "Retrieves a list of dividends by exchange ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved dividends for exchange",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DividendDto.class,
                                            example = "[{ \"stockTicker\": \"AAPL\", \"exchange\": \"NASDAQ\", \"exDate\": \"2024-02-09\", \"payDate\": \"2024-02-15\", \"payout\": \"0.24\" }, { \"stockTicker\": \"AAPL\", \"exchange\": \"NASDAQ\", \"exDate\": \"2023-11-10\", \"payDate\": \"2023-11-16\", \"payout\": \"0.24\" }]"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/exchangeId/{exchangeId}")
    public ResponseEntity<List<DividendDto>> getDividendsByExchangeId(@PathVariable Long exchangeId) {
        List<DividendDto> dividendDtoList = dividendService.getDividendsByExchangeId(exchangeId);
        return ResponseEntity.ok(dividendDtoList);
    }

    @Operation(
            summary = "Update a dividend by ID",
            description = "Updates a dividend's details by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dividend updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DividendDto.class,
                                            example = "{ \"stockTicker\": \"D05\", \"exchange\": \"SGX\", \"exDate\": \"2023-05-05\", \"payDate\": \"2023-05-19\", \"payout\": \"0.0307\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/id/{dividendId}")
    public ResponseEntity<DividendDto> updateDividendById(@PathVariable Long dividendId, @Valid @RequestBody DividendDto dividendDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            DividendDto updatedDividendDto = dividendService.updateDividendById(dividendId, dividendDto);
            return ResponseEntity.ok(updatedDividendDto);
        }
    }

    @Operation(
            summary = "Delete all dividends",
            description = "Deletes all dividends.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted all dividends",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted all dividends."
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllDividends() {
        dividendService.deleteAllDividends();
        return ResponseEntity.ok("Successfully deleted all dividends.");
    }

    @Operation(
            summary = "Delete dividend by ID",
            description = "Deletes a dividend by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted dividend",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted dividend with id: 1"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/id/{dividendId}")
    public ResponseEntity<String> deleteDividendById(@PathVariable Long dividendId) {
        dividendService.deleteDividendById(dividendId);
        return ResponseEntity.ok(String.format("Successfully deleted dividend with id: %d", dividendId));
    }
}