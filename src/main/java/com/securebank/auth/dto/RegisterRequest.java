package com.securebank.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @Size(min = 8, max = 64)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                 message = "password must contain upper, lower, digit and special")
        String password,
        @NotBlank String confirmPassword,
        @Valid Profile profile,
        @NotNull Boolean acceptTerms,
        @NotBlank @Pattern(regexp = "email|sms") String verificationMethod
) {
    public record Profile(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String mobile
    ) {
    }
}
