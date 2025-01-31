package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.service.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "Provides endpoints for managing users.")
public class UserController {
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Retrieve all users",
            description = "Fetches a list of all users in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserDto.class,
                                            example = "[{ \"username\": \"ccyh_97\", \"roles\": [\"ADMIN\", \"USER\"], \"firstName\": \"Caleb\", \"lastName\": \"Chan\", \"imagePath\": null, \"displayCurrency\": \"SGD\" }, { \"username\": \"bob_da_builderz\", \"roles\": [\"USER\"], \"firstName\": null, \"lastName\": null, \"imagePath\": null, \"displayCurrency\": null }]"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtoList = userService.getAllUsers();
        return ResponseEntity.ok(userDtoList);
    }

    @Operation(
            summary = "Retrieve user by ID",
            description = "Fetches the details of a user by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User details fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserDto.class,
                                            example = "{ \"username\": \"ccyh_97\", \"roles\": [\"ADMIN\", \"USER\"], \"firstName\": \"Caleb\", \"lastName\": \"Chan\", \"imagePath\": null, \"displayCurrency\": \"SGD\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @Operation(
            summary = "Update user by ID",
            description = "Updates the details of an existing user identified by their ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDto.class,
                                    example = "{ \"username\": \"bob_da_builderz\", \"roles\": [\"USER\"], \"firstName\": \"Bob\", \"lastName\": \"Builder\", \"imagePath\": \"bob_updated.png\", \"displayCurrency\": \"EUR\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated user by ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserDto.class,
                                            example = "{ \"username\": \"bob_da_builderz\", \"roles\": [\"USER\"], \"firstName\": \"Bob\", \"lastName\": \"Builder\", \"imagePath\": \"bob_updated.png\", \"displayCurrency\": \"EUR\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/id/{userId}")
    public ResponseEntity<UserDto> updateUserById(@PathVariable Long userId, @Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            UserDto updatedUserDto = userService.updateUserById(userId, userDto);
            return ResponseEntity.ok(updatedUserDto);
        }
    }

    @Operation(
            summary = "Delete all users",
            description = "Deletes all users in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted all users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"message\": \"Successfully deleted all users.\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("Successfully deleted all users.");
    }

    @Operation(
            summary = "Delete user by ID",
            description = "Deletes a user identified by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted user by ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"message\": \"Successfully deleted user with id: 1.\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') OR #userId == authentication.principal")
    @DeleteMapping("/delete/id/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok(String.format("Successfully deleted user with id: %d", userId));
    }
}