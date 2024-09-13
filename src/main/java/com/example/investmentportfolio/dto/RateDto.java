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
public class RateDto {
    @NotBlank(groups = CreateValidation.class, message = "Rate name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{3}/[a-zA-Z]{3}$", message = "Rate name should contain exactly 3 letters followed by a slash followed by another 3 letters.")
    private String rateName;

    @NotBlank(groups = CreateValidation.class, message = "Rate cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Rate should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String rate;
}
