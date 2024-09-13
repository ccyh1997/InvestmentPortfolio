package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long userId);
    UserDto getUserByUsername(String username);
    UserDto updateUserById(Long userId, UserDto userDto);
    UserDto updateUserByUsername(String username, UserDto userDto);
    void deleteAllUsers();
    void deleteUserById(Long userId);
    UserDto deleteUserByUsername(String username);
}
