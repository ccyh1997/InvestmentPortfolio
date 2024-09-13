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
public class UserDto {
    @NotBlank(groups = CreateValidation.class, message = "Username cannot be blank.")
    @Pattern(regexp = "^\\w{7,20}$", message = "Username must be between 7 and 20 characters and contain only letters, numbers, or underscores.")
    private String username;

    @NotBlank(groups = CreateValidation.class, message = "Password cannot be blank.")
    @Pattern(regexp = "^\\S{8,}$", message = "Password must be at least 8 characters long and contain no whitespace characters.")
    private String password;

    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{1,50}(?<!\\s)$", message = "First name must only contain letters and spaces, cannot exceed 50 characters, and must not start or end with whitespace.")
    private String firstName;

    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{1,50}(?<!\\s)$", message = "Last name must only contain letters and spaces, cannot exceed 50 characters, and must not start or end with whitespace.")
    private String lastName;

    @Pattern(regexp = "^\\S{1,20}\\.(jpg|jpeg|png)$", message = "Image path must not contain whitespace, end with .jpg, .jpeg, or .png, and have a maximum of 20 characters for the image name.")
    private String imagePath;

    @NotBlank(groups = CreateValidation.class, message = "Display currency cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]{3}$", message = "Display currency must be exactly 3 letters.")
    private String displayCurrency;
}
