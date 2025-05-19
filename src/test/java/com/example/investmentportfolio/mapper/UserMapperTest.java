package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void givenUser_whenConvertToDto_thenReturnUserDto() {
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "", null, null, null);
        UserDto userDto = userMapper.convertToDto(user);
        assertEquals("testUser", userDto.getUsername());
        assertNull(userDto.getPassword());
        assertEquals(Set.of("ADMIN", "USER"), userDto.getRoles());
        assertEquals("", userDto.getFirstName());
        assertNull(userDto.getLastName());
        assertNull(userDto.getImagePath());
        assertNull(userDto.getDisplayCurrency());
    }

    @Test
    void givenNullUser_whenConvertToDto_thenReturnNull() {
        UserDto userDto = userMapper.convertToDto(null);
        assertNull(userDto);
    }

    @Test
    void givenUserAndNullRoles_whenConvertToDto_thenReturnUserDto() {
        User user = new User(1L, "testUser", "securePassword123", null, "", null, null, null);
        UserDto userDto = userMapper.convertToDto(user);
        assertEquals("testUser", userDto.getUsername());
        assertNull(userDto.getPassword());
        assertNull(userDto.getRoles());
        assertEquals("", userDto.getFirstName());
        assertNull(userDto.getLastName());
        assertNull(userDto.getImagePath());
        assertNull(userDto.getDisplayCurrency());
    }

    @Test
    void givenUserDto_whenConvertToEntity_thenReturnUser() {
        UserDto userDto = new UserDto("testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        User user = userMapper.convertToEntity(userDto);
        assertEquals("testUser", user.getUsername());
        assertNull(user.getPassword());
        assertEquals(Set.of("ADMIN", "USER"), user.getRoles());
        assertEquals("Caleb", user.getFirstName());
        assertEquals("Chan", user.getLastName());
        assertNull(user.getImagePath());
        assertEquals("SGD", user.getDisplayCurrency());
    }

    @Test
    void givenNullUserDto_whenConvertToEntity_thenReturnNull() {
        User user = userMapper.convertToEntity(null);
        assertNull(user);
    }

    @Test
    void givenUserDtoAndNullRoles_whenConvertToEntity_thenReturnUser() {
        UserDto userDto = new UserDto("testUser", "securePassword123", null, "Caleb", "Chan", null, "SGD");
        User user = userMapper.convertToEntity(userDto);
        assertEquals("testUser", user.getUsername());
        assertNull(user.getPassword());
        assertNull(userDto.getRoles());
        assertEquals("Caleb", user.getFirstName());
        assertEquals("Chan", user.getLastName());
        assertNull(user.getImagePath());
        assertEquals("SGD", user.getDisplayCurrency());
    }

    @Test
    void givenUserDto_whenUpdateEntityWithDto_thenReturnUser() {
        UserDto userDto = new UserDto("testUser", "securePassword123", new HashSet<>(Set.of("ADMIN", "USER")), "Caleb", "Chan", "img.jpg", "SGD");
        User user = new User(1L, "testUser", "securePassword123", new HashSet<>(Set.of("ADMIN", "USER")), "Caleb", "Chan", null, "SGD");
        userMapper.updateEntityWithDto(userDto, user);
        assertEquals("testUser", user.getUsername());
        assertEquals("securePassword123", user.getPassword());
        assertEquals(Set.of("ADMIN", "USER"), user.getRoles());
        assertEquals("Caleb", user.getFirstName());
        assertEquals("Chan", user.getLastName());
        assertEquals("img.jpg", user.getImagePath());
        assertEquals("SGD", user.getDisplayCurrency());
    }

    @Test
    void givenNullUserDto_whenUpdateEntityWithDto_thenReturnNull() {
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        User updatedUser = userMapper.updateEntityWithDto(null, user);
        assertEquals(user, updatedUser);
    }

    @Test
    void givenNullUserDtoFields_whenUpdateEntityWithDto_thenSkipUpdate() {
        User user = new User(1L, "testUser", "securePassword123", Set.of("ADMIN", "USER"), "Caleb", "Chan", null, "SGD");
        UserDto userDto = new UserDto(null, null, null, null, null, null, null);
        User updatedUser = userMapper.updateEntityWithDto(userDto, user);
        assertEquals(user, updatedUser);
    }

    @Test
    void givenUserDtoAndNullRolesInEntity_whenUpdateEntityWithDto_thenReturnUser() {
        UserDto userDto = new UserDto("testUser", "securePassword123", new HashSet<>(Set.of("ADMIN", "USER")), "Caleb", "Chan", null, "SGD");
        User user = new User(1L, "testUser", "securePassword123", null, "Caleb", "Chan", null, "SGD");
        userMapper.updateEntityWithDto(userDto, user);
        assertEquals("testUser", user.getUsername());
        assertEquals("securePassword123", user.getPassword());
        assertEquals(Set.of("ADMIN", "USER"), user.getRoles());
        assertEquals("Caleb", user.getFirstName());
        assertEquals("Chan", user.getLastName());
        assertNull(user.getImagePath());
        assertEquals("SGD", user.getDisplayCurrency());
    }

    @Test
    void givenUserDtoAndNullRolesInBothEntityAndDto_whenUpdateEntityWithDto_thenReturnUser() {
        UserDto userDto = new UserDto("testUser", "securePassword123", null, "Caleb", "Chan", null, "SGD");
        User user = new User(1L, "testUser", "securePassword123", null, "Caleb", "Chan", null, "SGD");
        userMapper.updateEntityWithDto(userDto, user);
        assertEquals("testUser", user.getUsername());
        assertEquals("securePassword123", user.getPassword());
        assertNull(userDto.getRoles());
        assertEquals("Caleb", user.getFirstName());
        assertEquals("Chan", user.getLastName());
        assertNull(user.getImagePath());
        assertEquals("SGD", user.getDisplayCurrency());
    }
}