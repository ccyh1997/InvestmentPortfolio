package com.example.investmentportfolio.service;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.mapper.UserMapper;
import com.example.investmentportfolio.model.User;
import com.example.investmentportfolio.repository.UserRepository;
import com.example.investmentportfolio.service.impl.UserServiceImpl;
import com.example.investmentportfolio.util.NotFoundException;
import com.example.investmentportfolio.util.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.investmentportfolio.util.Constants.BAD_REQUEST_ERROR_CODE;
import static com.example.investmentportfolio.util.Constants.NOT_FOUND_ERROR_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void givenUsersExist_whenGetAllUsers_thenReturnUsers() {
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> retrievedUsers = userService.getAllUsers();
        assertEquals(1, retrievedUsers.size());
    }

    @Test
    void givenUsersDoNotExist_whenGetAllUsers_thenThrowNotFoundException() {
        when(userRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getAllUsers());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUserExists_whenGetUserById_thenReturnUser() {
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.getUserById(1L);
        verify(userMapper, times(1)).convertToDto(user);
    }

    @Test
    void givenUserDoesNotExist_whenGetUserById_thenThrowNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenValidRequestAndUserExists_whenUpdateUserById_thenUpdateUser() {
        UserDto requestUserDto = new UserDto("testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.updateEntityWithDto(any(), any())).thenReturn(user);
        userService.updateUserById(1L, requestUserDto);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenBadRequest_whenUpdateUserById_thenThrowValidationException() {
        UserDto requestUserDto = new UserDto("testUser", "", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.updateUserById(1L, requestUserDto));
        assertEquals(BAD_REQUEST_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("Password cannot be blank.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUserDoesNotExist_whenUpdateUserById_thenThrowNotFoundException() {
        UserDto requestUserDto = new UserDto("testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUserById(1L, requestUserDto));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUsersExist_whenDeleteAllUsers_thenDeleteUsers() {
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        userService.deleteAllUsers();
        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    void givenUsersDoNotExist_whenDeleteAllUsers_thenThrowNotFoundException() {
        when(userRepository.findAll()).thenReturn(List.of());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteAllUsers());
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user(s) found.", exception.getError().getErrorMessages().getFirst());
    }

    @Test
    void givenUserExists_whenDeleteUserById_thenDeleteUser() {
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.deleteUserById(any());
        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    void givenUserDoesNotExist_whenDeleteUserById_thenThrowNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUserById(1L));
        assertEquals(NOT_FOUND_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals("No user found with id: 1", exception.getError().getErrorMessages().getFirst());
    }
}