package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.StatisticDto;
import com.example.investmentportfolio.service.StatisticService;
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
@RequestMapping("/statistics")
@Tag(name = "Statistic Controller", description = "Provides endpoints for managing exchange statistics.")
public class StatisticController {
    private static final Logger LOGGER = LogManager.getLogger(StatisticController.class);
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @Operation(
            summary = "Create a new statistic",
            description = "Allows administrators to create a new statistic with the provided details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = StatisticDto.class,
                                    example = """
                                    {
                                      "username": "bob_da_builderz",
                                      "stockTicker": "d05",
                                      "exchange": "sgx",
                                      "totalUnits": "546.88",
                                      "totalCost": "103002.59210",
                                      "totalValue": "240112.90900",
                                      "realizedProfits": "30000.45010",
                                      "unrealizedProfits": "70021.22040",
                                      "dividendsEarned": "20100.110",
                                      "totalProfits": "140001.8302"
                                    }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Statistic successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StatisticDto.class,
                                            example = """
                                            {
                                              "username": "bob_da_builderz",
                                              "stockTicker": "D05",
                                              "exchange": "SGX",
                                              "totalUnits": "546.88",
                                              "totalCost": "103002.5921",
                                              "totalValue": "240112.909",
                                              "realizedProfits": "30000.4501",
                                              "unrealizedProfits": "70021.2204",
                                              "dividendsEarned": "20100.11",
                                              "totalProfits": "140001.8302"
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<StatisticDto> createStatistic(@Valid @RequestBody StatisticDto statisticDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StatisticDto createdStatisticDto = statisticService.createStatistic(statisticDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStatisticDto);
        }
    }

    @Operation(
            summary = "Get all statistics",
            description = "Retrieve a list of all statistics.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of statistics successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StatisticDto.class,
                                            example = """
                                            [
                                                {
                                                    "username": "ccyh_97",
                                                    "stockTicker": "D05",
                                                    "exchange": "SGX",
                                                    "totalUnits": "0.",
                                                    "totalCost": "2849.999266372",
                                                    "totalValue": "0.",
                                                    "realizedProfits": "25.7412606279999692825",
                                                    "unrealizedProfits": "0.",
                                                    "dividendsEarned": "42.0056022",
                                                    "totalProfits": "67.7468628279999692825"
                                                },
                                                {
                                                    "username": "ccyh_97",
                                                    "stockTicker": "CSPX",
                                                    "exchange": "LSE",
                                                    "totalUnits": "42.3585",
                                                    "totalCost": "28671.68134377",
                                                    "totalValue": "36790.443321408",
                                                    "realizedProfits": "0",
                                                    "unrealizedProfits": "8118.7619776379999909055",
                                                    "dividendsEarned": "0",
                                                    "totalProfits": "8118.7619776379999909055"
                                                },
                                                {
                                                    "username": "ccyh_97",
                                                    "stockTicker": "AAPL",
                                                    "exchange": "NASDAQ",
                                                    "totalUnits": "1.7722",
                                                    "totalCost": "450.86003854",
                                                    "totalValue": "555.6001819392",
                                                    "realizedProfits": "0",
                                                    "unrealizedProfits": "104.7401433992000002696",
                                                    "dividendsEarned": "1.7394213888",
                                                    "totalProfits": "106.4795647880000002696"
                                                },
                                                {
                                                    "username": "bob_da_builderz",
                                                    "stockTicker": "D05",
                                                    "exchange": "SGX",
                                                    "totalUnits": "546.88",
                                                    "totalCost": "103002.5921",
                                                    "totalValue": "240112.909",
                                                    "realizedProfits": "30000.4501",
                                                    "unrealizedProfits": "70021.2204",
                                                    "dividendsEarned": "20100.11",
                                                    "totalProfits": "140001.8302"
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
    public ResponseEntity<List<StatisticDto>> getAllStatistics() {
        List<StatisticDto> statisticDtoList = statisticService.getAllStatistics();
        return ResponseEntity.ok(statisticDtoList);
    }

    @Operation(
            summary = "Get statistic by ID",
            description = "Retrieve a statistic by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Statistic successfully retrieved by ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StatisticDto.class,
                                            example = """
                                            {
                                                "username": "ccyh_97",
                                                "stockTicker": "D05",
                                                "exchange": "SGX",
                                                "totalUnits": "0.",
                                                "totalCost": "2849.999266372",
                                                "totalValue": "0.",
                                                "realizedProfits": "25.7412606279999692825",
                                                "unrealizedProfits": "0.",
                                                "dividendsEarned": "42.0056022",
                                                "totalProfits": "67.7468628279999692825"
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/id/{statisticId}")
    public ResponseEntity<StatisticDto> getStatisticById(@PathVariable Long statisticId) {
        StatisticDto statisticDto = statisticService.getStatisticById(statisticId);
        return ResponseEntity.ok(statisticDto);
    }

    @Operation(
            summary = "Get statistics by user ID",
            description = "Retrieve statistics by user ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of statistics successfully retrieved for a user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StatisticDto.class,
                                            example = """
                                            [
                                                {
                                                    "username": "ccyh_97",
                                                    "stockTicker": "D05",
                                                    "exchange": "SGX",
                                                    "totalUnits": "0.",
                                                    "totalCost": "2849.999266372",
                                                    "totalValue": "0.",
                                                    "realizedProfits": "25.7412606279999692825",
                                                    "unrealizedProfits": "0.",
                                                    "dividendsEarned": "42.0056022",
                                                    "totalProfits": "67.7468628279999692825"
                                                },
                                                {
                                                    "username": "ccyh_97",
                                                    "stockTicker": "CSPX",
                                                    "exchange": "LSE",
                                                    "totalUnits": "42.3585",
                                                    "totalCost": "28671.68134377",
                                                    "totalValue": "36790.443321408",
                                                    "realizedProfits": "0",
                                                    "unrealizedProfits": "8118.7619776379999909055",
                                                    "dividendsEarned": "0",
                                                    "totalProfits": "8118.7619776379999909055"
                                                },
                                                {
                                                    "username": "ccyh_97",
                                                    "stockTicker": "AAPL",
                                                    "exchange": "NASDAQ",
                                                    "totalUnits": "1.7722",
                                                    "totalCost": "450.86003854",
                                                    "totalValue": "555.6001819392",
                                                    "realizedProfits": "0",
                                                    "unrealizedProfits": "104.7401433992000002696",
                                                    "dividendsEarned": "1.7394213888",
                                                    "totalProfits": "106.4795647880000002696"
                                                }
                                            ]
                                            """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<StatisticDto>> getStatisticsByUserId(@PathVariable Long userId) {
        List<StatisticDto> statisticDtoList = statisticService.getStatisticsByUserId(userId);
        return ResponseEntity.ok(statisticDtoList);
    }

    @Operation(
            summary = "Update a statistic by ID",
            description = "Allows administrators to update a statistic by its ID with the provided details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = StatisticDto.class,
                                    example = """
                                    {
                                      "username": "ccyh_97",
                                      "stockTicker": "d05",
                                      "exchange": "sgx",
                                      "totalUnits": "546.88006546",
                                      "totalCost": "103002.592101",
                                      "totalValue": "240112.908006",
                                      "realizedProfits": "3001110.45010",
                                      "unrealizedProfits": "103.22040",
                                      "dividendsEarned": "111.110",
                                      "totalProfits": "2"
                                    }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Statistic successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StatisticDto.class,
                                            example = """
                                            {
                                              "username": "ccyh_97",
                                              "stockTicker": "D05",
                                              "exchange": "SGX",
                                              "totalUnits": "546.88006546",
                                              "totalCost": "103002.592101",
                                              "totalValue": "240112.908006",
                                              "realizedProfits": "3001110.4501",
                                              "unrealizedProfits": "103.2204",
                                              "dividendsEarned": "111.11",
                                              "totalProfits": "2"
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/id/{statisticId}")
    public ResponseEntity<StatisticDto> updateStatisticById(@PathVariable Long statisticId, @Valid @RequestBody StatisticDto statisticDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StatisticDto updatedStatisticDto = statisticService.updateStatisticById(statisticId, statisticDto);
            return ResponseEntity.ok(updatedStatisticDto);
        }
    }

    @Operation(
            summary = "Delete all statistics",
            description = "Allows administrators to delete all statistics.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All statistics successfully deleted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted all statistics."
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllStatistics() {
        statisticService.deleteAllStatistics();
        return ResponseEntity.ok("Successfully deleted all statistics.");
    }

    @Operation(
            summary = "Delete a statistic by ID",
            description = "Allows administrators to delete a statistic by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Statistic successfully deleted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted statistic with id: 1"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/id/{statisticId}")
    public ResponseEntity<String> deleteStatisticById(@PathVariable Long statisticId) {
        statisticService.deleteStatisticById(statisticId);
        return ResponseEntity.ok(String.format("Successfully deleted statistic with id: %d", statisticId));
    }

    @Operation(
            summary = "Delete statistics by user ID",
            description = "Allows administrators to delete statistics by user ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Statistics successfully deleted for the given user",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "Successfully deleted statistics with user id: 1"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/userId/{userId}")
    public ResponseEntity<String> deleteStatisticsByUserId(@PathVariable Long userId) {
        statisticService.deleteStatisticsByUserId(userId);
        return ResponseEntity.ok(String.format("Successfully deleted statistics with user id: %d", userId));
    }
}