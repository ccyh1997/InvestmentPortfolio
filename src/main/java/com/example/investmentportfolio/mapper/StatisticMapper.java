package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.StatisticDto;
import com.example.investmentportfolio.model.Statistic;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatisticMapper {
    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "totalUnits", source = "totalUnits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalCost", source = "totalCost", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalValue", source = "totalValue", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "realizedProfits", source = "realizedProfits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "unrealizedProfits", source = "unrealizedProfits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "dividendsEarned", source = "dividendsEarned", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalProfits", source = "totalProfits", qualifiedByName = "trimTrailingZeros")
    StatisticDto convertToDto(Statistic statistic);

    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "totalUnits", source = "totalUnits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalCost", source = "totalCost", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalValue", source = "totalValue", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "realizedProfits", source = "realizedProfits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "unrealizedProfits", source = "unrealizedProfits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "dividendsEarned", source = "dividendsEarned", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalProfits", source = "totalProfits", qualifiedByName = "trimTrailingZeros")
    Statistic convertToEntity(StatisticDto statisticDto);

    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "totalUnits", source = "totalUnits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalCost", source = "totalCost", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalValue", source = "totalValue", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "realizedProfits", source = "realizedProfits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "unrealizedProfits", source = "unrealizedProfits", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "dividendsEarned", source = "dividendsEarned", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "totalProfits", source = "totalProfits", qualifiedByName = "trimTrailingZeros")
    Statistic updateEntityWithDto(StatisticDto statisticDto, @MappingTarget Statistic statistic);

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