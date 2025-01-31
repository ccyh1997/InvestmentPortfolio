package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto updateUserById(Long userId, UserDto userDto);

    void deleteAllUsers();

    void deleteUserById(Long userId);
}
