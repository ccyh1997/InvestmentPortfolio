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
@Schema(description = "DTO representing dividend information for a stock.")
public class DividendDto {

    @NotBlank(groups = CreateValidation.class, message = "Stock ticker cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,10}$", message = "Stock ticker must only contain letters or numbers and be up to 10 characters.")
    @Schema(description = "Stock ticker symbol. Must contain only letters or numbers, with a maximum length of 10 characters.", example = "AAPL")
    private String stockTicker;

    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    @Schema(description = "Exchange name. Must contain only letters, with a maximum length of 10 characters.", example = "NASDAQ")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Ex date cannot be blank.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Ex date must be in the format yyyy-MM-dd")
    @Schema(description = "Ex-dividend date in the format yyyy-MM-dd.", example = "2025-01-20")
    private String exDate;

    @NotBlank(groups = CreateValidation.class, message = "Pay date cannot be blank.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Pay date must be in the format yyyy-MM-dd")
    @Schema(description = "Pay date in the format yyyy-MM-dd.", example = "2025-02-01")
    private String payDate;

    @NotBlank(groups = CreateValidation.class, message = "Payout cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Payout should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    @Schema(description = "Dividend payout amount. Can include up to one decimal point, with a maximum of 10 digits before and after the decimal.", example = "1.50")
    private String payout;
}