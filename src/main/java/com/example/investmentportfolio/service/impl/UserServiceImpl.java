package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.mapper.UserMapper;
import com.example.investmentportfolio.model.User;
import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.service.UserService;
import com.example.investmentportfolio.util.CreateValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.investmentportfolio.util.Constants.*;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validator validator;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return users.stream().map(userMapper::convertToDto).toList();
        } else {
            throw returnNotFoundException(LOGGER, NO_USERS_FOUND);
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return userMapper.convertToDto(optionalUser.get());
        } else {
            throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_ID, userId);
        }
    }

    @Override
    public UserDto updateUserById(Long userId, UserDto userDto) {
        validateRequestDto(userDto);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User updatedUser = userMapper.updateEntityWithDto(userDto, optionalUser.get());
            userRepository.save(updatedUser);
            return userMapper.convertToDto(updatedUser);
        } else {
            throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_ID, userId);
        }
    }

    @Override
    @Transactional
    public void deleteAllUsers() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            userRepository.deleteAll();
        } else {
            throw returnNotFoundException(LOGGER, NO_USERS_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw returnNotFoundException(LOGGER, NO_USER_FOUND_WITH_ID, userId);
        }
    }

    private void validateRequestDto(UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            throw returnValidationException(LOGGER, violations);
        }
    }
}
