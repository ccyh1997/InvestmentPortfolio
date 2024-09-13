package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.service.UserService;
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
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            UserDto createdUserDto = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
        }
    }

    // READ
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtoList = userService.getAllUsers();
        return ResponseEntity.ok(userDtoList);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.getUserByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    // UPDATE
    @PostMapping("/update/id/{userId}")
    public ResponseEntity<UserDto> updateUserById(@PathVariable Long userId, @Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            UserDto updatedUserDto = userService.updateUserById(userId, userDto);
            return ResponseEntity.ok(updatedUserDto);
        }
    }

    @PostMapping("/update/username/{username}")
    public ResponseEntity<UserDto> updateUserByUsername(@PathVariable String username, @Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            UserDto updatedUserDto = userService.updateUserByUsername(username, userDto);
            return ResponseEntity.ok(updatedUserDto);
        }
    }

    // DELETE
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("Successfully deleted all users.");
    }

    @DeleteMapping("/delete/id/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok(String.format("Successfully deleted user with id: %d", userId));
    }

    @DeleteMapping("/delete/username/{username}")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.deleteUserByUsername(username);
        return ResponseEntity.ok(String.format("Successfully deleted user with username: %s", userDto.getUsername()));
    }
}
