package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.DividendDto;
import com.example.investmentportfolio.model.Dividend;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DividendMapper {
    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "payout", source = "payout", qualifiedByName = "trimTrailingZeros")
    DividendDto convertToDto(Dividend dividend);

    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "payout", source = "payout", qualifiedByName = "trimTrailingZeros")
    Dividend convertToEntity(DividendDto dividendDto);

    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "payout", source = "payout", qualifiedByName = "trimTrailingZeros")
    Dividend updateEntityWithDto(DividendDto dividendDto, @MappingTarget Dividend dividend);

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