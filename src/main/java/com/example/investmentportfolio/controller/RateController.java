package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.service.RateService;
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
@RequestMapping("/rates")
@Tag(name = "Rate Controller", description = "Provides endpoints for managing exchange rates.")
public class RateController {
    private static final Logger LOGGER = LogManager.getLogger(RateController.class);
    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @Operation(
            summary = "Create a new rate",
            description = "Create a new exchange rate with the provided details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = RateDto.class,
                                    example = "{ \"rateName\": \"sgd/usd\", \"rate\": \"0.74120251\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Rate successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RateDto.class,
                                            example = "{ \"rateName\": \"SGD/USD\", \"rate\": \"0.74120251\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<RateDto> createRate(@Valid @RequestBody RateDto rateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            RateDto createdRateDto = rateService.createRate(rateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRateDto);
        }
    }

    @Operation(
            summary = "Get all rates",
            description = "Retrieves a list of all exchange rates.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of rates successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RateDto.class,
                                            example = "[{ \"rateName\": \"USD/SGD\", \"rate\": \"1.3632\" }, { \"rateName\": \"SGD/USD\", \"rate\": \"0.7336\" }]"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<RateDto>> getAllRates() {
        List<RateDto> rateDtoList = rateService.getAllRates();
        return ResponseEntity.ok(rateDtoList);
    }

    @Operation(
            summary = "Get rate by ID",
            description = "Retrieves an exchange rate by its unique identifier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Rate successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RateDto.class,
                                            example = "{ \"rateName\": \"USD/SGD\", \"rate\": \"1.3632\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id/{rateId}")
    public ResponseEntity<RateDto> getRateById(@PathVariable Long rateId) {
        RateDto rateDto = rateService.getRateById(rateId);
        return ResponseEntity.ok(rateDto);
    }

    @Operation(
            summary = "Update a rate by ID",
            description = "Update an exchange rate by its ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = RateDto.class,
                                    example = "{ \"rateName\": \"sgd/usd\", \"rate\": \"0.7412005100\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Rate successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RateDto.class,
                                            example = "{ \"rateName\": \"SGD/USD\", \"rate\": \"0.74120051\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/id/{rateId}")
    public ResponseEntity<RateDto> updateRateById(@PathVariable Long rateId, @Valid @RequestBody RateDto rateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            RateDto updatedRateDto = rateService.updateRateById(rateId, rateDto);
            return ResponseEntity.ok(updatedRateDto);
        }
    }

    @Operation(
            summary = "Delete all rates",
            description = "Deletes all exchange rates.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All rates successfully deleted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted all rates."
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllRates() {
        rateService.deleteAllRates();
        return ResponseEntity.ok("Successfully deleted all rates.");
    }

    @Operation(
            summary = "Delete rate by ID",
            description = "Deletes an exchange rate by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Rate successfully deleted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted rate with id: 1"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/id/{rateId}")
    public ResponseEntity<String> deleteRateById(@PathVariable Long rateId) {
        rateService.deleteRateById(rateId);
        return ResponseEntity.ok(String.format("Successfully deleted rate with id: %d", rateId));
    }
}