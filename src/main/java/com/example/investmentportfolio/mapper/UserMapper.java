package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.model.User;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "displayCurrency", source = "displayCurrency", qualifiedByName = "toUpperCase")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "capitalizeName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "capitalizeName")
    UserDto convertToDto(User user);

    @Mapping(target = "displayCurrency", source = "displayCurrency", qualifiedByName = "toUpperCase")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "capitalizeName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "capitalizeName")
    User convertToEntity(UserDto userDto);

    @Mapping(target = "displayCurrency", source = "displayCurrency", qualifiedByName = "toUpperCase")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "capitalizeName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "capitalizeName")
    User updateEntityWithDto(UserDto userDto, @MappingTarget User user);

    @Named("toUpperCase")
    default String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }

    @Named("capitalizeName")
    default String capitalizeName(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Arrays.stream(value.split("\\s+"))
                .map(word -> !word.isEmpty() ? Character.toUpperCase(word.charAt(0)) + word.substring(1) : "")
                .collect(Collectors.joining(" "));
    }

    @AfterMapping
    default void maskPassword(User user, @MappingTarget UserDto userDto) {
        if (user.getPassword() != null) {
            String maskedPassword = "*".repeat(user.getPassword().length());
            userDto.setPassword(maskedPassword);
        }
    }
}