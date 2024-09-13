package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.mapper.UserMapper;
import com.example.investmentportfolio.model.User;
import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.service.UserService;
import com.example.investmentportfolio.util.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
    public static final String NO_USER_FOUND_WITH_ID = "No user found with id: %d";
    public static final String NO_USER_FOUND_WITH_USERNAME = "No user found with username: %s";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validator validator;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        }
        else {
            User user = userMapper.convertToEntity(userDto);
            if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
                List<String> errorMessages = Collections.singletonList("An user with the same username already exists.");
                LOGGER.error(errorMessages);
                throw new AlreadyExistsException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
            } else {
                userRepository.save(user);
                return userMapper.convertToDto(user);
            }
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return users.stream()
                    .map(userMapper::convertToDto)
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No user(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return userMapper.convertToDto(optionalUser.get());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public UserDto getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isPresent()) {
            return userMapper.convertToDto(optionalUser.get());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_USERNAME, username));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public UserDto updateUserById(Long userId, UserDto userDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User updatedUser = userMapper.updateEntityWithDto(userDto, optionalUser.get());
            userRepository.save(updatedUser);
            return userMapper.convertToDto(updatedUser);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public UserDto updateUserByUsername(String username, UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isPresent()) {
            User updatedUser = userMapper.updateEntityWithDto(userDto, optionalUser.get());
            userRepository.save(updatedUser);
            return userMapper.convertToDto(updatedUser);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_USERNAME, username));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteAllUsers() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            userRepository.deleteAll();
        } else {
            List<String> errorMessages = Collections.singletonList("No user(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public UserDto deleteUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isPresent()) {
            userRepository.deleteByUsernameIgnoreCase(username);
            return userMapper.convertToDto(optionalUser.get());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_USERNAME, username));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }
}
