package com.example.investmentportfolio.dto;

import com.example.investmentportfolio.util.CreateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO representing the exchange rate between two currencies.")
public class RateDto {

    @NotBlank(groups = CreateValidation.class, message = "Rate name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{3}/[a-zA-Z]{3}$", message = "Rate name should contain exactly 3 letters followed by a slash followed by another 3 letters.")
    @Schema(description = "The rate name, formatted as 'XXX/YYY' where both 'XXX' and 'YYY' are exactly 3 uppercase letters.", example = "USD/SGD")
    private String rateName;

    @NotBlank(groups = CreateValidation.class, message = "Rate cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Rate should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The exchange rate value. Can include up to 10 digits before the decimal point and up to 10 digits after.", example = "1.23456789")
    private String rate;
}