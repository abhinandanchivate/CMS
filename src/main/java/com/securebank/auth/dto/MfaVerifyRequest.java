package com.securebank.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MfaVerifyRequest(@NotBlank @Size(min = 6, max = 6) String code) {
}
