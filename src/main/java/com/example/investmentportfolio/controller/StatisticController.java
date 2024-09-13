package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.StatisticDto;
import com.example.investmentportfolio.service.StatisticService;
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
@RequestMapping("/statistics")
public class StatisticController {
    private static final Logger LOGGER = LogManager.getLogger(StatisticController.class);
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<StatisticDto> createStatistic(@Valid @RequestBody StatisticDto statisticDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StatisticDto createdStatisticDto = statisticService.createStatistic(statisticDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStatisticDto);
        }
    }

    // READ
    @GetMapping("/all")
    public ResponseEntity<List<StatisticDto>> getAllStatistics() {
        List<StatisticDto> statisticDtoList = statisticService.getAllStatistics();
        return ResponseEntity.ok(statisticDtoList);
    }

    @GetMapping("/id/{statisticId}")
    public ResponseEntity<StatisticDto> getStatisticById(@PathVariable Long statisticId) {
        StatisticDto statisticDto = statisticService.getStatisticById(statisticId);
        return ResponseEntity.ok(statisticDto);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<StatisticDto>> getStatisticsByUserId(@PathVariable Long userId) {
        List<StatisticDto> statisticDtoList = statisticService.getStatisticsByUserId(userId);
        return ResponseEntity.ok(statisticDtoList);
    }

    // UPDATE
    @PostMapping("/update/id/{statisticId}")
    public ResponseEntity<StatisticDto> updateStatisticById(@PathVariable Long statisticId, @Valid @RequestBody StatisticDto statisticDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            StatisticDto updatedStatisticDto = statisticService.updateStatisticById(statisticId, statisticDto);
            return ResponseEntity.ok(updatedStatisticDto);
        }
    }

    // DELETE
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllStatistics() {
        statisticService.deleteAllStatistics();
        return ResponseEntity.ok("Successfully deleted all statistics.");
    }

    @DeleteMapping("/delete/id/{statisticId}")
    public ResponseEntity<String> deleteStatisticById(@PathVariable Long statisticId) {
        statisticService.deleteStatisticById(statisticId);
        return ResponseEntity.ok(String.format("Successfully deleted statistic with id: %d", statisticId));
    }

    @DeleteMapping("/delete/userId/{userId}")
    public ResponseEntity<String> deleteStatisticsByUserId(@PathVariable Long userId) {
        statisticService.deleteStatisticsByUserId(userId);
        return ResponseEntity.ok(String.format("Successfully deleted statistics with user id: %d", userId));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        statisticService.updateStatisticsForAllUsers();
        return ResponseEntity.ok("");
    }
}