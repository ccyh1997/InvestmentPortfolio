package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.service.StockService;
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
@RequestMapping("/stocks")
@Tag(name = "Stock Controller", description = "Provides endpoints for managing exchange stocks.")
public class StockController {
    private static final Logger LOGGER = LogManager.getLogger(StockController.class);
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @Operation(
            summary = "Create a new stock",
            description = "Create a new stock with the provided details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = StockDto.class,
                                    example = """
                                    {
                                      "stockTicker": "ov8",
                                      "stockName": "sheng Siong group Ltd",
                                      "stockType": "Equity",
                                      "exchange": "sgx",
                                      "lastPrice": "1.5306078000",
                                      "baseCurrency": "sgd",
                                      "divInd": "y",
                                      "delistInd": "n"
                                    }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Stock created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StockDto.class,
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<StockDto> createStock(@Valid @RequestBody StockDto stockDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StockDto createdStockDto = stockService.createStock(stockDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStockDto);
        }
    }

    @Operation(
            summary = "Get all stocks",
            description = "Retrieve a list of all stocks.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all stocks",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StockDto.class,
                                            example = """
                                            [
                                              {
                                                "stockTicker": "D05",
                                                "stockName": "DBS Group Holdings Ltd",
                                                "stockType": "Equity",
                                                "exchange": "SGX",
                                                "lastPrice": "43.62",
                                                "baseCurrency": "SGD",
                                                "divInd": "Y",
                                                "delistInd": "N"
                                              },
                                              {
                                                "stockTicker": "CSPX",
                                                "stockName": "IShares Core S&P 500 ETF",
                                                "stockType": "ETF",
                                                "exchange": "LSE",
                                                "lastPrice": "638.00",
                                                "baseCurrency": "USD",
                                                "divInd": "N",
                                                "delistInd": "N"
                                              }
                                            ]
                                            """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<StockDto>> getAllStocks() {
        List<StockDto> stockDtoList = stockService.getAllStocks();
        return ResponseEntity.ok(stockDtoList);
    }

    @Operation(
            summary = "Get stock by ID",
            description = "Retrieve a stock based on the provided ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StockDto.class,
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id/{stockId}")
    public ResponseEntity<StockDto> getStockById(@PathVariable Long stockId) {
        StockDto stockDto = stockService.getStockById(stockId);
        return ResponseEntity.ok(stockDto);
    }

    @Operation(
            summary = "Get stock by ticker",
            description = "Retrieve a stock based on the provided stock ticker.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StockDto.class,
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ticker/{stockTicker}")
    public ResponseEntity<StockDto> getStockByTicker(@PathVariable String stockTicker) {
        StockDto stockDto = stockService.getStockByTicker(stockTicker);
        return ResponseEntity.ok(stockDto);
    }

    @Operation(
            summary = "Retrieve stocks by multiple filters",
            description = "Retrieve stocks based on various filters including stock type, exchange ID, dividend indicator, and delist indicator.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stocks retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StockDto.class,
                                            example = """
                                            [
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
                                            ]
                                        """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StockDto>> getStocksByFilters(
            @RequestParam(required = false) Long exchangeId,
            @RequestParam(required = false) String stockType,
            @RequestParam(required = false) String divInd,
            @RequestParam(required = false) String delistInd) {
        List<StockDto> stockDtoList = stockService.getStocksByFilters(exchangeId, stockType, divInd, delistInd);
        return ResponseEntity.ok(stockDtoList);
    }

    @Operation(
            summary = "Update a stock by ID",
            description = "Update the details of a stock by its ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = StockDto.class,
                                    example = """
                                        {
                                          "stockTicker": "ov8",
                                          "stockName": "Sheng Siong Group Ltd",
                                          "stockType": "Equity",
                                          "exchange": "SGX",
                                          "lastPrice": "1.63",
                                          "baseCurrency": "SGD",
                                          "divInd": "Y",
                                          "delistInd": "N"
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StockDto.class,
                                            example = """
                                                {
                                                  "stockTicker": "OV8",
                                                  "stockName": "Sheng Siong Group Ltd",
                                                  "stockType": "Equity",
                                                  "exchange": "SGX",
                                                  "lastPrice": "1.63",
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/id/{stockId}")
    public ResponseEntity<StockDto> updateStockById(@PathVariable Long stockId, @Valid @RequestBody StockDto stockDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StockDto updatedStockDto = stockService.updateStockById(stockId, stockDto);
            return ResponseEntity.ok(updatedStockDto);
        }
    }

    @Operation(
            summary = "Update stock by ticker",
            description = "Update stock details using the stock ticker.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = StockDto.class,
                                    example = """
                                        {
                                            "stockTicker": "ov8",
                                            "stockName": "sheng Siong group Ltd",
                                            "stockType": "Equity",
                                            "exchange": "lse",
                                            "lastPrice": "1.63",
                                            "baseCurrency": "sgd",
                                            "divInd": "y",
                                            "delistInd": "n"
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StockDto.class,
                                            example = """
                                                {
                                                    "stockTicker": "OV8",
                                                    "stockName": "Sheng Siong Group Ltd",
                                                    "stockType": "Equity",
                                                    "exchange": "LSE",
                                                    "lastPrice": "1.63",
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/ticker/{stockTicker}")
    public ResponseEntity<StockDto> updateStockByTicker(@PathVariable String stockTicker, @Valid @RequestBody StockDto stockDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StockDto updatedStockDto = stockService.updateStockByTicker(stockTicker, stockDto);
            return ResponseEntity.ok(updatedStockDto);
        }
    }

    @Operation(
            summary = "Delete all stocks",
            description = "Delete all stocks from the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted all stocks",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted all stocks"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllStocks() {
        stockService.deleteAllStocks();
        return ResponseEntity.ok("Successfully deleted all stocks");
    }

    @Operation(
            summary = "Delete stock by ID",
            description = "Delete a specific stock using the stock ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock successfully deleted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted stock with id: 1"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/id/{stockId}")
    public ResponseEntity<String> deleteStockById(@PathVariable Long stockId) {
        stockService.deleteStockById(stockId);
        return ResponseEntity.ok(String.format("Successfully deleted stock with id: %d", stockId));
    }

    @Operation(
            summary = "Delete stock by ticker",
            description = "Delete a specific stock using the stock ticker.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock successfully deleted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted stock with ticker: OV8"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/ticker/{stockTicker}")
    public ResponseEntity<String> deleteStockByTicker(@PathVariable String stockTicker) {
        StockDto stockDto = stockService.deleteStockByTicker(stockTicker);
        return ResponseEntity.ok(String.format("Successfully deleted stock with ticker: %s", stockTicker));
    }
}
