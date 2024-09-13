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
public class TransactionDto {
    @NotBlank(groups = CreateValidation.class, message = "Username cannot be blank.")
    @Pattern(regexp = "^\\w{7,20}$", message = "Username must be between 7 and 20 characters and contain only letters, numbers, or underscores.")
    private String username;

    @NotBlank(groups = CreateValidation.class, message = "Transaction date cannot be blank.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Transaction date must be in the format yyyy-MM-dd")
    private String transactionDate;

    @NotBlank(groups = CreateValidation.class, message = "Transaction type cannot be blank.")
    @Pattern(regexp = "^(buy|sell)$", message = "Transaction type must be either 'buy' or 'sell'")
    private String transactionType;

    @NotBlank(groups = CreateValidation.class, message = "Stock ticker cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,10}$", message = "Stock ticker must only contain letters or numbers and be up to 10 characters.")
    private String stockTicker;

    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Units cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Units should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String units;

    @NotBlank(groups = CreateValidation.class, message = "Unit price cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Unit price should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String unitPrice;

    @NotBlank(groups = CreateValidation.class, message = "Fees cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Fees should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String fees;

    @NotBlank(groups = CreateValidation.class, message = "Currency cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{3}$", message = "Currency must be exactly 3 letters.")
    private String currency;
}
