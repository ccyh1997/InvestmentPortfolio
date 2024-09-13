package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.model.Stock;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockMapper {
    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "stockName", source = "stockName", qualifiedByName = "capitalize")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "lastPrice", source = "lastPrice", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "baseCurrency", source = "baseCurrency", qualifiedByName = "toUpperCase")
    @Mapping(target = "divInd", source = "divInd", qualifiedByName = "toUpperCase")
    @Mapping(target = "delistInd", source = "delistInd", qualifiedByName = "toUpperCase")
    StockDto convertToDto(Stock stock);

    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "stockName", source = "stockName", qualifiedByName = "capitalize")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "lastPrice", source = "lastPrice", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "baseCurrency", source = "baseCurrency", qualifiedByName = "toUpperCase")
    @Mapping(target = "divInd", source = "divInd", qualifiedByName = "toUpperCase")
    @Mapping(target = "delistInd", source = "delistInd", qualifiedByName = "toUpperCase")
    Stock convertToEntity(StockDto stockDto);

    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "stockName", source = "stockName", qualifiedByName = "capitalize")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "lastPrice", source = "lastPrice", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "baseCurrency", source = "baseCurrency", qualifiedByName = "toUpperCase")
    @Mapping(target = "divInd", source = "divInd", qualifiedByName = "toUpperCase")
    @Mapping(target = "delistInd", source = "delistInd", qualifiedByName = "toUpperCase")
    Stock updateEntityWithDto(StockDto stockDto, @MappingTarget Stock stock);

    @Named("toUpperCase")
    default String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }

    @Named("capitalize")
    default String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Arrays.stream(value.split("\\s+"))
                .map(word -> !word.isEmpty() ? Character.toUpperCase(word.charAt(0)) + word.substring(1) : "")
                .collect(Collectors.joining(" "));
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