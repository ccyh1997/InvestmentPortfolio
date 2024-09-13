package com.example.investmentportfolio.mapper;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.model.Exchange;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExchangeMapper {
    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "countryCode", source = "countryCode", qualifiedByName = "toUpperCase")
    @Mapping(target = "suffix", source = "suffix", qualifiedByName = "toUpperCase")
    ExchangeDto convertToDto(Exchange exchange);

    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "countryCode", source = "countryCode", qualifiedByName = "toUpperCase")
    @Mapping(target = "suffix", source = "suffix", qualifiedByName = "toUpperCase")
    Exchange convertToEntity(ExchangeDto exchangeDto);

    @Mapping(target = "exchange", source = "exchange", qualifiedByName = "toUpperCase")
    @Mapping(target = "countryCode", source = "countryCode", qualifiedByName = "toUpperCase")
    @Mapping(target = "suffix", source = "suffix", qualifiedByName = "toUpperCase")
    Exchange updateEntityWithDto(ExchangeDto exchangeDto, @MappingTarget Exchange exchange);

    @Named("toUpperCase")
    default String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }
}