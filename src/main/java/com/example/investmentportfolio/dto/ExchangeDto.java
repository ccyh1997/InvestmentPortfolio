package com.example.investmentportfolio.dto;

import com.example.investmentportfolio.util.CreateValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeDto {
    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Country code cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{2}$", message = "Country code must be exactly 2 characters and contain only letters.")
    private String countryCode;

    @NotBlank(groups = CreateValidation.class, message = "Suffix cannot be blank.")
    @Pattern(regexp = "^\\.[a-zA-Z]{0,4}$", message = "Suffix must start with a . followed by up to 4 letters.")
    private String suffix;
}