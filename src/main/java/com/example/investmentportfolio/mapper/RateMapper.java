package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.model.Rate;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RateMapper {
    @Mapping(target = "rateName", source = "rateName", qualifiedByName = "toUpperCase")
    @Mapping(target = "rate", source = "rate", qualifiedByName = "trimTrailingZeros")
    RateDto convertToDto(Rate rate);
    @Mapping(target = "rateName", source = "rateName", qualifiedByName = "toUpperCase")
    @Mapping(target = "rate", source = "rate", qualifiedByName = "trimTrailingZeros")
    Rate convertToEntity(RateDto rateDto);
    @Mapping(target = "rateName", source = "rateName", qualifiedByName = "toUpperCase")
    @Mapping(target = "rate", source = "rate", qualifiedByName = "trimTrailingZeros")
    Rate updateEntityWithDto(RateDto rateDto, @MappingTarget Rate rate);

    @Named("toUpperCase")
    default String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }

    @Named("trimTrailingZeros")
    default String trimTrailingZeros(String value) {
        List<String> parts = Arrays.asList(value.split("\\."));
        if (parts.size() >= 2) {
            List<String> parts2 = new ArrayList<>(List.of(parts.get(1).split("0")));
            return parts.get(0).concat(".").concat(String.join("0", parts2));
        }
        return value;
    }
}