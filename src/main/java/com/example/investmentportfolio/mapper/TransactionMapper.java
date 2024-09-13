package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.TransactionDto;
import com.example.investmentportfolio.model.Transaction;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {
    @Mapping(target = "transactionType", source = "transactionType", qualifiedByName = "capitalize")
    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "units", source = "units", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "unitPrice", source = "unitPrice", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "fees", source = "fees", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "currency", source = "currency", qualifiedByName = "toUpperCase")
    TransactionDto convertToDto(Transaction transaction);

    @Mapping(target = "transactionType", source = "transactionType", qualifiedByName = "capitalize")
    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "units", source = "units", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "unitPrice", source = "unitPrice", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "fees", source = "fees", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "currency", source = "currency", qualifiedByName = "toUpperCase")
    Transaction convertToEntity(TransactionDto transactionDto);

    @Mapping(target = "transactionType", source = "transactionType", qualifiedByName = "capitalize")
    @Mapping(target = "stockTicker", source = "stockTicker", qualifiedByName = "toUpperCase")
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "units", source = "units", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "unitPrice", source = "unitPrice", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "fees", source = "fees", qualifiedByName = "trimTrailingZeros")
    @Mapping(target = "currency", source = "currency", qualifiedByName = "toUpperCase")
    Transaction updateEntityWithDto(TransactionDto transactionDto, @MappingTarget Transaction transaction);

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