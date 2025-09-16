package com.securebank.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificationRequest(
        @Email @NotBlank String email,
        @NotBlank String code
) {
}
