package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.service.ExchangeService;
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
@RequestMapping("/exchanges")
@Tag(name = "Exchange Controller", description = "Provides endpoints for managing exchanges.")
public class ExchangeController {
    private static final Logger LOGGER = LogManager.getLogger(ExchangeController.class);
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Operation(
            summary = "Create a new exchange",
            description = "Creates a new exchange entry in the database.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ExchangeDto.class,
                                    example = "{ \"exchange\": \"sehk\", \"countryCode\": \"hk\", \"suffix\": \".hk\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created exchange",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ExchangeDto.class,
                                            example = "{ \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ExchangeDto> createExchange(@Valid @RequestBody ExchangeDto exchangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            ExchangeDto createdExchangeDto = exchangeService.createExchange(exchangeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExchangeDto);
        }
    }

    @Operation(
            summary = "Retrieve all exchanges",
            description = "Fetches a list of all exchanges.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all exchanges",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ExchangeDto.class,
                                            example = "[ { \"exchange\": \"NASDAQ\", \"countryCode\": \"US\", \"suffix\": null }, { \"exchange\": \"NYSE\", \"countryCode\": \"US\", \"suffix\": null }, { \"exchange\": \"SGX\", \"countryCode\": \"SG\", \"suffix\": \".SI\" }, { \"exchange\": \"LSE\", \"countryCode\": \"UK\", \"suffix\": \".L\" }, { \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" } ]"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/all")
    public ResponseEntity<List<ExchangeDto>> getAllExchanges() {
        List<ExchangeDto> exchangeDtoList = exchangeService.getAllExchanges();
        return ResponseEntity.ok(exchangeDtoList);
    }

    @Operation(
            summary = "Retrieve an exchange by ID",
            description = "Fetches a specific exchange by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved exchange",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ExchangeDto.class,
                                            example = "{ \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/id/{exchangeId}")
    public ResponseEntity<ExchangeDto> getExchangeById(@PathVariable Long exchangeId) {
        ExchangeDto exchangeDto = exchangeService.getExchangeById(exchangeId);
        return ResponseEntity.ok(exchangeDto);
    }

    @Operation(
            summary = "Retrieve exchanges by country code",
            description = "Fetches exchanges that belong to the specified country.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved exchanges by country code",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ExchangeDto.class,
                                            example = "[ { \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" } ]"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/country/{countryCode}")
    public ResponseEntity<List<ExchangeDto>> getExchangesByCountryCode(@PathVariable String countryCode) {
        List<ExchangeDto> exchangeDtoList = exchangeService.getExchangesByCountryCode(countryCode);
        return ResponseEntity.ok(exchangeDtoList);
    }

    @Operation(
            summary = "Retrieve an exchange by suffix",
            description = "Fetches an exchange that matches the specified suffix.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved exchange by suffix",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ExchangeDto.class,
                                            example = "{ \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/suffix/{suffix}")
    public ResponseEntity<ExchangeDto> getExchangeBySuffix(@PathVariable String suffix) {
        ExchangeDto exchangeDto = exchangeService.getExchangeBySuffix(suffix);
        return ResponseEntity.ok(exchangeDto);
    }

    @Operation(
            summary = "Update an exchange by ID",
            description = "Updates an existing exchange by its ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ExchangeDto.class,
                                    example = "{ \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated exchange",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ExchangeDto.class,
                                            example = "{ \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/id/{exchangeId}")
    public ResponseEntity<ExchangeDto> updateExchangeById(@PathVariable Long exchangeId, @Valid @RequestBody ExchangeDto exchangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            ExchangeDto updatedExchangeDto = exchangeService.updateExchangeById(exchangeId, exchangeDto);
            return ResponseEntity.ok(updatedExchangeDto);
        }
    }

    @Operation(
            summary = "Update an exchange by suffix",
            description = "Updates an existing exchange by its suffix.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ExchangeDto.class,
                                    example = "{ \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated exchange",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ExchangeDto.class,
                                            example = "{ \"exchange\": \"SEHK\", \"countryCode\": \"HK\", \"suffix\": \".HK\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/suffix/{suffix}")
    public ResponseEntity<ExchangeDto> updateExchangeBySuffix(@PathVariable String suffix, @Valid @RequestBody ExchangeDto exchangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            ExchangeDto updatedExchangeDto = exchangeService.updateExchangeBySuffix(suffix, exchangeDto);
            return ResponseEntity.ok(updatedExchangeDto);
        }
    }

    @Operation(
            summary = "Delete all exchanges",
            description = "Deletes all exchange entries.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted all exchanges",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted all exchanges"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllExchanges() {
        exchangeService.deleteAllExchanges();
        return ResponseEntity.ok("Successfully deleted all exchanges.");
    }

    @Operation(
            summary = "Delete an exchange by ID",
            description = "Deletes an exchange by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted exchange",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted exchange with id: 1"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/id/{exchangeId}")
    public ResponseEntity<String> deleteExchangeById(@PathVariable Long exchangeId) {
        exchangeService.deleteExchangeById(exchangeId);
        return ResponseEntity.ok(String.format("Successfully deleted exchange with id: %d", exchangeId));
    }

    @Operation(
            summary = "Delete an exchange by suffix",
            description = "Deletes an exchange by its suffix.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted exchange",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted exchange with suffix: .HK"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/suffix/{suffix}")
    public ResponseEntity<String> deleteExchangeBySuffix(@PathVariable String suffix) {
        exchangeService.deleteExchangeBySuffix(suffix);
        return ResponseEntity.ok(String.format("Successfully deleted exchange with suffix: %s", suffix));
    }
}