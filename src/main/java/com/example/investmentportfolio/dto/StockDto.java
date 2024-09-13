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
public class StockDto {
    @NotBlank(groups = CreateValidation.class, message = "Stock ticker cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,10}$", message = "Stock ticker must only contain letters or numbers and be up to 10 characters.")
    private String stockTicker;

    @NotBlank(groups = CreateValidation.class, message = "Stock name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9. ]{1,100}$", message = "Stock name must only contain letters, numbers, full stop, and be up to 100 characters.")
    private String stockName;

    @NotBlank(groups = CreateValidation.class, message = "Stock type cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,50}$", message = "Stock type must only contain letters and be up to 50 characters.")
    private String stockType;

    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Last price cannot be blank.")
    @Pattern(regexp = "^(?!.*\\..*\\.)\\d{1,10}(\\.\\d{1,10})?$", message = "Last price should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters.")
    private String lastPrice;

    @NotBlank(groups = CreateValidation.class, message = "Base currency cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{3}$", message = "Base currency must be exactly 3 letters.")
    private String baseCurrency;

    @NotBlank(groups = CreateValidation.class, message = "Dividend indicator cannot be blank.")
    @Pattern(regexp = "[YyNn]", message = "Dividend indicator should be either 'Y' or 'N'.")
    private String divInd;

    @NotBlank(groups = CreateValidation.class, message = "Delist indicator cannot be blank.")
    @Pattern(regexp = "[YyNn]", message = "Delist indicator should be either 'Y' or 'N'.")
    private String delistInd;
}
