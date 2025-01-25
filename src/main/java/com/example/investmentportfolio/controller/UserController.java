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
import org.springframework.http.HttpStatus;
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
            summary = "Create a new user",
            description = "Creates a new user entry in the database.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDto.class,
                                    example = "{ \"username\": \"bob_da_builderz\", \"password\": \"lalaland\", \"firstName\": \"bob\", \"lastName\": \"da Builder\", \"imagePath\": \"bob.png\", \"displayCurrency\": \"usd\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserDto.class,
                                            example = "{ \"username\": \"bob_da_builderzzz\", \"password\": \"********\", \"roles\": null, \"firstName\": \"Bob\", \"lastName\": \"Da Builder\", \"imagePath\": \"bob.png\", \"displayCurrency\": \"USD\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            UserDto createdUserDto = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
        }
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
                                            example = "[{ \"username\": \"ccyh_97\", \"password\": \"********\", \"roles\": [\"ADMIN\", \"USER\"], \"firstName\": \"Caleb\", \"lastName\": \"Chan\", \"imagePath\": null, \"displayCurrency\": \"SGD\" }, { \"username\": \"bob_da_builderz\", \"password\": \"********\", \"roles\": [\"USER\"], \"firstName\": null, \"lastName\": null, \"imagePath\": null, \"displayCurrency\": null }]"
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
                                            example = "{ \"username\": \"ccyh_97\", \"password\": \"********\", \"roles\": [\"ADMIN\", \"USER\"], \"firstName\": \"Caleb\", \"lastName\": \"Chan\", \"imagePath\": null, \"displayCurrency\": \"SGD\" }"
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
            summary = "Retrieve user by username",
            description = "Fetches the details of a user by their username.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User details fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserDto.class,
                                            example = "{ \"username\": \"bob_da_builderz\", \"password\": \"********\", \"roles\": [\"USER\"], \"firstName\": null, \"lastName\": null, \"imagePath\": null, \"displayCurrency\": null }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.getUserByUsername(username);
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
                                    example = "{ \"username\": \"bob_da_builderz\", \"password\": \"newpassword123\", \"roles\": [\"USER\"], \"firstName\": \"Bob\", \"lastName\": \"Builder\", \"imagePath\": \"bob_updated.png\", \"displayCurrency\": \"EUR\" }"
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
                                            example = "{ \"username\": \"bob_da_builderz\", \"password\": \"********\", \"roles\": [\"USER\"], \"firstName\": \"Bob\", \"lastName\": \"Builder\", \"imagePath\": \"bob_updated.png\", \"displayCurrency\": \"EUR\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
            summary = "Update user by username",
            description = "Updates the details of an existing user identified by their username.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDto.class,
                                    example = "{ \"username\": \"bob_da_builderz\", \"password\": \"newpassword123\", \"roles\": [\"USER\"], \"firstName\": \"Bob\", \"lastName\": \"Builder\", \"imagePath\": \"bob_updated.png\", \"displayCurrency\": \"EUR\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated user by username",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserDto.class,
                                            example = "{ \"username\": \"bob_da_builderz\", \"password\": \"********\", \"roles\": [\"USER\"], \"firstName\": \"Bob\", \"lastName\": \"Builder\", \"imagePath\": \"bob_updated.png\", \"displayCurrency\": \"EUR\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/update/username/{username}")
    public ResponseEntity<UserDto> updateUserByUsername(@PathVariable String username, @Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            UserDto updatedUserDto = userService.updateUserByUsername(username, userDto);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/delete/id/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok(String.format("Successfully deleted user with id: %d", userId));
    }

    @Operation(
            summary = "Delete user by username",
            description = "Deletes a user identified by their username.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted user by username",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"message\": \"Successfully deleted user with username: bob_da_builderz.\" }"
                                    )
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/delete/username/{username}")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.deleteUserByUsername(username);
        return ResponseEntity.ok(String.format("Successfully deleted user with username: %s", userDto.getUsername()));
    }
}