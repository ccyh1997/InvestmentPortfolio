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
public class StatisticDto {
    @NotBlank(groups = CreateValidation.class, message = "Username cannot be blank.")
    @Pattern(regexp = "^\\w{7,20}$", message = "Username must be between 7 and 20 characters and contain only letters, numbers, or underscores.")
    private String username;

    @NotBlank(groups = CreateValidation.class, message = "Stock ticker cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,10}$", message = "Stock ticker must only contain letters or numbers and be up to 10 characters.")
    private String stockTicker;

    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Total units cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total units should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String totalUnits;

    @NotBlank(groups = CreateValidation.class, message = "Total cost cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total cost should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String totalCost;

    @NotBlank(groups = CreateValidation.class, message = "Total value cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total value should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String totalValue;

    @NotBlank(groups = CreateValidation.class, message = "Realized profits cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Realized profits should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String realizedProfits;

    @NotBlank(groups = CreateValidation.class, message = "Unrealized profits cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Unrealized profits should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String unrealizedProfits;

    @NotBlank(groups = CreateValidation.class, message = "Dividends earned cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Dividends earned should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String dividendsEarned;

    @NotBlank(groups = CreateValidation.class, message = "Total profits cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total profits should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String totalProfits;
}
