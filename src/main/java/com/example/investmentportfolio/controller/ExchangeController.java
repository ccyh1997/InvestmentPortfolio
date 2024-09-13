package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.service.ExchangeService;
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
@RequestMapping("/exchanges")
public class ExchangeController {
    private static final Logger LOGGER = LogManager.getLogger(ExchangeController.class);
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<ExchangeDto> createExchange(@Valid @RequestBody ExchangeDto exchangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            ExchangeDto createdExchangeDto = exchangeService.createExchange(exchangeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExchangeDto);
        }
    }

    // READ
    @GetMapping("/all")
    public ResponseEntity<List<ExchangeDto>> getAllExchanges() {
        List<ExchangeDto> exchangeDtoList = exchangeService.getAllExchanges();
        return ResponseEntity.ok(exchangeDtoList);
    }

    @GetMapping("/id/{exchangeId}")
    public ResponseEntity<ExchangeDto> getExchangeById(@PathVariable Long exchangeId) {
        ExchangeDto exchangeDto = exchangeService.getExchangeById(exchangeId);
        return ResponseEntity.ok(exchangeDto);
    }

    @GetMapping("/country/{countryCode}")
    public ResponseEntity<List<ExchangeDto>> getExchangesByCountryCode(@PathVariable String countryCode) {
        List<ExchangeDto> exchangeDtoList = exchangeService.getExchangesByCountryCode(countryCode);
        return ResponseEntity.ok(exchangeDtoList);
    }

    @GetMapping("/suffix/{suffix}")
    public ResponseEntity<ExchangeDto> getExchangeBySuffix(@PathVariable String suffix) {
        ExchangeDto exchangeDto = exchangeService.getExchangeBySuffix(suffix);
        return ResponseEntity.ok(exchangeDto);
    }

    // UPDATE
    @PostMapping("/update/id/{exchangeId}")
    public ResponseEntity<ExchangeDto> updateExchangeById(@PathVariable Long exchangeId, @Valid @RequestBody ExchangeDto exchangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            ExchangeDto updatedExchangeDto = exchangeService.updateExchangeById(exchangeId, exchangeDto);
            return ResponseEntity.ok(updatedExchangeDto);
        }
    }

    @PostMapping("/update/suffix/{suffix}")
    public ResponseEntity<ExchangeDto> updateExchangeBySuffix(@PathVariable String suffix, @Valid @RequestBody ExchangeDto exchangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            ExchangeDto updatedExchangeDto = exchangeService.updateExchangeBySuffix(suffix, exchangeDto);
            return ResponseEntity.ok(updatedExchangeDto);
        }
    }

    // DELETE
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllExchanges() {
        exchangeService.deleteAllExchanges();
        return ResponseEntity.ok("Successfully deleted all exchanges.");
    }

    @DeleteMapping("/delete/id/{exchangeId}")
    public ResponseEntity<String> deleteExchangeById(@PathVariable Long exchangeId) {
        exchangeService.deleteExchangeById(exchangeId);
        return ResponseEntity.ok(String.format("Successfully deleted exchange with id: %d", exchangeId));
    }

    @DeleteMapping("/delete/suffix/{suffix}")
    public ResponseEntity<String> deleteExchangeBySuffix(@PathVariable String suffix) {
        ExchangeDto exchangeDto = exchangeService.deleteExchangeBySuffix(suffix);
        return ResponseEntity.ok(String.format("Successfully deleted exchange with suffix: %s", exchangeDto.getSuffix()));
    }
}
