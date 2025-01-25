package com.example.investmentportfolio.dto;

import com.example.investmentportfolio.util.CreateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a user in the investment portfolio system, including user credentials, personal information, and assigned roles.")
public class UserDto {

    @NotBlank(groups = CreateValidation.class, message = "Username cannot be blank.")
    @Pattern(regexp = "^\\w{7,20}$", message = "Username must be between 7 and 20 characters and contain only letters, numbers, or underscores.")
    @Schema(description = "The user's username, which must be between 7 and 20 characters long and contain only letters, numbers, or underscores.", example = "user123")
    private String username;

    @NotBlank(groups = CreateValidation.class, message = "Password cannot be blank.")
    @Pattern(regexp = "^\\S{8,}$", message = "Password must be at least 8 characters long and contain no whitespace characters.")
    @Schema(description = "The user's password, which must be at least 8 characters long and contain no whitespace characters.", example = "password123")
    private String password;

    @Schema(description = "A set of roles assigned to the user.", example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;

    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{1,50}(?<!\\s)$", message = "First name must only contain letters and spaces, cannot exceed 50 characters, and must not start or end with whitespace.")
    @Schema(description = "The user's first name, which can only contain letters and spaces, must not exceed 50 characters, and cannot start or end with whitespace.", example = "John")
    private String firstName;

    @Pattern(regexp = "^(?!\\s)[a-zA-Z\\s]{1,50}(?<!\\s)$", message = "Last name must only contain letters and spaces, cannot exceed 50 characters, and must not start or end with whitespace.")
    @Schema(description = "The user's last name, which can only contain letters and spaces, must not exceed 50 characters, and cannot start or end with whitespace.", example = "Doe")
    private String lastName;

    @Pattern(regexp = "^\\S{1,20}\\.(jpg|jpeg|png)$", message = "Image path must not contain whitespace, end with .jpg, .jpeg, or .png, and the image name must not exceed 20 characters.")
    @Schema(description = "The path to the user's image, which must not contain whitespace, must end with .jpg, .jpeg, or .png, and the image name must not exceed 20 characters.", example = "user123.jpg")
    private String imagePath;

    @Pattern(regexp = "^[a-zA-Z]{3}$", message = "Display currency must be exactly 3 letters.")
    @Schema(description = "The 3-letter code for the user's display currency.", example = "USD")
    private String displayCurrency;
}