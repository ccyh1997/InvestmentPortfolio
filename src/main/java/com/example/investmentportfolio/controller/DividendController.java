package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.DividendDto;
import com.example.investmentportfolio.service.DividendService;
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
@RequestMapping("/dividends")
public class DividendController {
    private static final Logger LOGGER = LogManager.getLogger(DividendController.class);
    private final DividendService dividendService;

    public DividendController(DividendService dividendService) {
        this.dividendService = dividendService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<DividendDto> createDividend(@Valid @RequestBody DividendDto dividendDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            DividendDto createdDividendDto = dividendService.createDividend(dividendDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDividendDto);
        }
    }

    // READ
    @GetMapping("/all")
    public ResponseEntity<List<DividendDto>> getAllDividends() {
        List<DividendDto> dividendDtoList = dividendService.getAllDividends();
        return ResponseEntity.ok(dividendDtoList);
    }

    @GetMapping("/id/{dividendId}")
    public ResponseEntity<DividendDto> getDividendById(@PathVariable Long dividendId) {
        DividendDto dividendDto = dividendService.getDividendById(dividendId);
        return ResponseEntity.ok(dividendDto);
    }

    @GetMapping("/stockId/{stockId}")
    public ResponseEntity<List<DividendDto>> getDividendsByStockId(@PathVariable Long stockId) {
        List<DividendDto> dividendDtoList = dividendService.getDividendsByStockId(stockId);
        return ResponseEntity.ok(dividendDtoList);
    }

    @GetMapping("/exchangeId/{exchangeId}")
    public ResponseEntity<List<DividendDto>> getDividendsByExchangeId(@PathVariable Long exchangeId) {
        List<DividendDto> dividendDtoList = dividendService.getDividendsByExchangeId(exchangeId);
        return ResponseEntity.ok(dividendDtoList);
    }

    // UPDATE
    @PostMapping("/update/id/{dividendId}")
    public ResponseEntity<DividendDto> updateDividendById(@PathVariable Long dividendId, @Valid @RequestBody DividendDto dividendDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            DividendDto updatedDividendDto = dividendService.updateDividendById(dividendId, dividendDto);
            return ResponseEntity.ok(updatedDividendDto);
        }
    }

    // DELETE
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllDividends() {
        dividendService.deleteAllDividends();
        return ResponseEntity.ok("Successfully deleted all dividends.");
    }

    @DeleteMapping("/delete/id/{dividendId}")
    public ResponseEntity<String> deleteDividendById(@PathVariable Long dividendId) {
        dividendService.deleteDividendById(dividendId);
        return ResponseEntity.ok(String.format("Successfully deleted dividend with id: %d", dividendId));
    }
}
