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
@Schema(description = "DTO representing transaction details in an investment portfolio.")
public class TransactionDto {

    @NotBlank(groups = CreateValidation.class, message = "Username cannot be blank.")
    @Pattern(regexp = "^\\w{7,20}$", message = "Username must be between 7 and 20 characters and contain only letters, numbers, or underscores.")
    @Schema(description = "The username of the user performing the transaction.", example = "user123")
    private String username;

    @NotBlank(groups = CreateValidation.class, message = "Transaction date cannot be blank.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Transaction date must follow the format yyyy-MM-dd.")
    @Schema(description = "The date of the transaction in the format yyyy-MM-dd.", example = "2025-01-01")
    private String transactionDate;

    @NotBlank(groups = CreateValidation.class, message = "Transaction type cannot be blank.")
    @Pattern(regexp = "^(?i)(buy|sell)$", message = "Transaction type must be either 'Buy' or 'Sell'.")
    @Schema(description = "The type of transaction, either 'Buy' or 'Sell'.", example = "Buy")
    private String transactionType;

    @NotBlank(groups = CreateValidation.class, message = "Stock ticker cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,10}$", message = "Stock ticker must only contain letters or numbers and be up to 10 characters.")
    @Schema(description = "The stock ticker symbol representing the stock involved in the transaction.", example = "AAPL")
    private String stockTicker;

    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    @Schema(description = "The name of the exchange where the stock is traded.", example = "NASDAQ")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Units cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Units should have at most one decimal point, with up to 10 digits before and after the decimal point.")
    @Schema(description = "The number of units of stock involved in the transaction.", example = "100")
    private String units;

    @NotBlank(groups = CreateValidation.class, message = "Unit price cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Unit price should have at most one decimal point, with up to 10 digits before and after the decimal point.")
    @Schema(description = "The price per unit of the stock in the transaction.", example = "150.25")
    private String unitPrice;

    @NotBlank(groups = CreateValidation.class, message = "Fees cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Fees should have at most one decimal point, with up to 10 digits before and after the decimal point.")
    @Schema(description = "The transaction fees applicable to the transaction.", example = "5.00")
    private String fees;

    @NotBlank(groups = CreateValidation.class, message = "Currency cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{3}$", message = "Currency must be exactly 3 uppercase letters.")
    @Schema(description = "The currency used for the transaction, represented by its 3-letter code.", example = "USD")
    private String currency;
}