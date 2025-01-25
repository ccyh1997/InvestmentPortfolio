package com.example.investmentportfolio.security;

import com.example.investmentportfolio.dto.UserDto;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Provides endpoints for user registration, login, and authentication.")
public class AuthenticationController {
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user with the provided username and password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDto.class,
                                    example = "{ \"username\": \"testuser\", \"password\": \"testpassword\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User registered successfully",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            example = "You’re all set! Your registration is complete, and you can proceed to log in."
                                    )
                            )
                    ),
            }
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            return ResponseEntity.ok(authenticationService.registerUser(userDto.getUsername(), userDto.getPassword()));
        }
    }

    @Operation(
            summary = "Login and generate JWT and refresh token",
            description = "Login with valid username and password to receive JWT and refresh tokens.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDto.class,
                                    example = "{ \"username\": \"testuser\", \"password\": \"testpassword\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged in successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = AuthenticationDto.class,
                                            example = "{ \"jwt\": \"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzYzMDksImV4cCI6MTczNzM3NzIwOX0.dPngL6I917bXHEKfF181Y541C62qMhnM0Oey59q66ew\", \"refreshToken\": \"b4b788b2-989a-4b4a-8631-12f94460ff2a\" }"
                                    )
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationDto> login(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            AuthenticationDto authenticationDto = authenticationService.generateTokens(userDto.getUsername(), userDto.getPassword());
            return ResponseEntity.ok(authenticationDto);
        }
    }

    @Operation(
            summary = "Refresh JWT using refresh token",
            description = "Refreshes the JWT token using the provided refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AuthenticationDto.class,
                                    example = "{ \"jwt\": \"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzYzMDksImV4cCI6MTczNzM3NzIwOX0.dPngL6I917bXHEKfF181Y541C62qMhnM0Oey59q66ew\", \"refreshToken\": \"b4b788b2-989a-4b4a-8631-12f94460ff2a\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "JWT refresh successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = AuthenticationDto.class,
                                            example = "{ \"jwt\": \"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInN1YiI6ImNjeWhfOTciLCJpYXQiOjE3MzczNzY1NjUsImV4cCI6MTczNzM3NzQ2NX0.odp-y0_PZ97ZWOpVLN5wyvf4rXS1p6fEuBPUoEOYKgY\"}"
                                    )
                            )
                    )
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationDto> refresh(@Valid @RequestBody AuthenticationDto authenticationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            AuthenticationDto authenticationDtoNew = authenticationService.regenerateJwt(authenticationDto.getJwt(), authenticationDto.getRefreshToken());
            return ResponseEntity.ok(authenticationDtoNew);
        }
    }
}
