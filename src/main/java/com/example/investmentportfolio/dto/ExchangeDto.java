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
@Schema(description = "DTO representing details of a stock exchange.")
public class ExchangeDto {

    @NotBlank(groups = CreateValidation.class, message = "Exchange name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{1,10}$", message = "Exchange name must only contain letters and be up to 10 characters.")
    @Schema(description = "Name of the exchange. Must contain only letters and be up to 10 characters long.", example = "NASDAQ")
    private String exchange;

    @NotBlank(groups = CreateValidation.class, message = "Country code cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{2}$", message = "Country code must be exactly 2 characters and contain only letters.")
    @Schema(description = "Country code of the exchange. Must be exactly 2 letters.", example = "US")
    private String countryCode;

    @NotBlank(groups = CreateValidation.class, message = "Suffix cannot be blank.")
    @Pattern(regexp = "^\\.[a-zA-Z]{0,4}$", message = "Suffix must start with a . followed by up to 4 letters.")
    @Schema(description = "Exchange suffix. Must start with a '.' and be followed by up to 4 letters.", example = ".SGX")
    private String suffix;
}