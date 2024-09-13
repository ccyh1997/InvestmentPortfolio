package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.service.RateService;
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
@RequestMapping("/rates")
public class RateController {
    private static final Logger LOGGER = LogManager.getLogger(RateController.class);
    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<RateDto> createRate(@Valid @RequestBody RateDto rateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            RateDto createdRateDto = rateService.createRate(rateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRateDto);
        }
    }

    // READ
    @GetMapping("/all")
    public ResponseEntity<List<RateDto>> getAllRates() {
        List<RateDto> rateDtoList = rateService.getAllRates();
        return ResponseEntity.ok(rateDtoList);
    }

    @GetMapping("/id/{rateId}")
    public ResponseEntity<RateDto> getRateById(@PathVariable Long rateId) {
        RateDto rateDto = rateService.getRateById(rateId);
        return ResponseEntity.ok(rateDto);
    }

    // UPDATE
    @PostMapping("/update/id/{rateId}")
    public ResponseEntity<RateDto> updateRateById(@PathVariable Long rateId, @Valid @RequestBody RateDto rateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            RateDto updatedRateDto = rateService.updateRateById(rateId, rateDto);
            return ResponseEntity.ok(updatedRateDto);
        }
    }

    // DELETE
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllRates() {
        rateService.deleteAllRates();
        return ResponseEntity.ok("Successfully deleted all rates.");
    }

    @DeleteMapping("/delete/id/{rateId}")
    public ResponseEntity<String> deleteRateById(@PathVariable Long rateId) {
        rateService.deleteRateById(rateId);
        return ResponseEntity.ok(String.format("Successfully deleted rate with id: %d", rateId));
    }
}
