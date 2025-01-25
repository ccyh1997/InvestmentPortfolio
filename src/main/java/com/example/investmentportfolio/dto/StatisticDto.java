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
@Schema(description = "DTO representing statistical information for an investment portfolio.")
public class StatisticDto {

    @NotBlank(groups = CreateValidation.class, message = "Username cannot be blank.")
    @Pattern(regexp = "^\\w{7,20}$", message = "Username must be between 7 and 20 characters and contain only letters, numbers, or underscores.")
    @Schema(description = "The username associated with the investment portfolio.", example = "john_doe123")
    private String username;

    @NotBlank(groups = CreateValidation.class, message = "Stock ticker cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,10}$", message = "Stock ticker must only contain letters or numbers and be up to 10 characters.")
    @Schema(description = "The stock ticker symbol representing the stock.", example = "AAPL")
    private String stockTicker;

    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    @Schema(description = "The exchange where the stock is traded.", example = "NASDAQ")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Total units cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total units should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The total number of units held in the portfolio.", example = "150.5")
    private String totalUnits;

    @NotBlank(groups = CreateValidation.class, message = "Total cost cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total cost should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The total cost of the units in the portfolio.", example = "5000.25")
    private String totalCost;

    @NotBlank(groups = CreateValidation.class, message = "Total value cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total value should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The total market value of the units in the portfolio.", example = "5500.75")
    private String totalValue;

    @NotBlank(groups = CreateValidation.class, message = "Realized profits cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Realized profits should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The realized profits from the investment portfolio.", example = "500.50")
    private String realizedProfits;

    @NotBlank(groups = CreateValidation.class, message = "Unrealized profits cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Unrealized profits should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The unrealized profits from the investment portfolio.", example = "200.30")
    private String unrealizedProfits;

    @NotBlank(groups = CreateValidation.class, message = "Dividends earned cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Dividends earned should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The total dividends earned from the investment portfolio.", example = "50.75")
    private String dividendsEarned;

    @NotBlank(groups = CreateValidation.class, message = "Total profits cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Total profits should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "The total profits from the investment portfolio.", example = "700.80")
    private String totalProfits;
}