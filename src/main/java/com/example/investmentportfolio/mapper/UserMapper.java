package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.model.User;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "capitalize")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "capitalize")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "displayCurrency", source = "displayCurrency", qualifiedByName = "toUpperCase")
    UserDto convertToDto(User user);

    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "capitalize")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "capitalize")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "displayCurrency", source = "displayCurrency", qualifiedByName = "toUpperCase")
    User convertToEntity(UserDto userDto);

    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "capitalize")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "capitalize")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "displayCurrency", source = "displayCurrency", qualifiedByName = "toUpperCase")
    User updateEntityWithDto(UserDto userDto, @MappingTarget User user);

    @Named("toUpperCase")
    default String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }

    @Named("capitalize")
    default String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Arrays.stream(value.split("\\s+")).map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1)).collect(Collectors.joining(" "));
    }
}