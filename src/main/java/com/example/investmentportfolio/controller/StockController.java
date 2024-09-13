package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.service.StockService;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {
    private static final Logger LOGGER = LogManager.getLogger(StockController.class);
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<StockDto> createStock(@Valid @RequestBody StockDto stockDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StockDto createdStockDto = stockService.createStock(stockDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStockDto);
        }
    }

    // READ
    @GetMapping("/all")
    public ResponseEntity<List<StockDto>> getAllStocks() {
        List<StockDto> stockDtoList = stockService.getAllStocks();
        return ResponseEntity.ok(stockDtoList);
    }

    @GetMapping("/id/{stockId}")
    public ResponseEntity<StockDto> getStockById(@PathVariable Long stockId) {
        StockDto stockDto = stockService.getStockById(stockId);
        return ResponseEntity.ok(stockDto);
    }

    @GetMapping("/ticker/{stockTicker}")
    public ResponseEntity<StockDto> getStockByTicker(@PathVariable String stockTicker) {
        StockDto stockDto = stockService.getStockByTicker(stockTicker);
        return ResponseEntity.ok(stockDto);
    }

    @GetMapping("/type/{stockType}")
    public ResponseEntity<List<StockDto>> getStocksByType(@PathVariable String stockType) {
        List<StockDto> stockDtoList = stockService.getStocksByType(stockType);
        return ResponseEntity.ok(stockDtoList);
    }

    @GetMapping("/exchangeId/{exchangeId}")
    public ResponseEntity<List<StockDto>> getStocksByExchangeId(@PathVariable Long exchangeId) {
        List<StockDto> stockDtoList = stockService.getStocksByExchangeId(exchangeId);
        return ResponseEntity.ok(stockDtoList);
    }

    @GetMapping("/divInd/{divInd}")
    public ResponseEntity<List<StockDto>> getStocksByDividendIndicator(@PathVariable String divInd) {
        List<StockDto> stockDtoList = stockService.getStocksByDividendIndicator(divInd);
        return ResponseEntity.ok(stockDtoList);
    }

    @GetMapping("/delistInd/{delistInd}")
    public ResponseEntity<List<StockDto>> getStocksByDelistIndicator(@PathVariable String delistInd) {
        List<StockDto> stockDtoList = stockService.getStocksByDelistIndicator(delistInd);
        return ResponseEntity.ok(stockDtoList);
    }

    // UPDATE
    @PostMapping("/update/id/{stockId}")
    public ResponseEntity<StockDto> updateStockById(@PathVariable Long stockId, @Valid @RequestBody StockDto stockDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StockDto updatedStockDto = stockService.updateStockById(stockId, stockDto);
            return ResponseEntity.ok(updatedStockDto);
        }
    }

    @PostMapping("/update/ticker/{stockTicker}")
    public ResponseEntity<StockDto> updateStockByTicker(@PathVariable String stockTicker, @Valid @RequestBody StockDto stockDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StockDto updatedStockDto = stockService.updateStockByTicker(stockTicker, stockDto);
            return ResponseEntity.ok(updatedStockDto);
        }
    }

    // DELETE
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllStocks() {
        stockService.deleteAllStocks();
        return ResponseEntity.ok("Successfully deleted all stocks");
    }

    @DeleteMapping("/delete/id/{stockId}")
    public ResponseEntity<String> deleteStockById(@PathVariable Long stockId) {
        stockService.deleteStockById(stockId);
        return ResponseEntity.ok(String.format("Successfully deleted stock with id: %d", stockId));
    }

    @DeleteMapping("/delete/ticker/{stockTicker}")
    public ResponseEntity<String> deleteStockByTicker(@PathVariable String stockTicker) {
        StockDto stockDto = stockService.deleteStockByTicker(stockTicker);
        return ResponseEntity.ok(String.format("Successfully deleted stock with ticker: %s", stockDto.getStockTicker()));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() throws IOException, URISyntaxException {
        stockService.updateLiveStockPrices();
        return ResponseEntity.ok("");
    }
}
