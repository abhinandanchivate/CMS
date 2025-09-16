package com.securebank.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @Size(min = 6, max = 6) String mfaCode,
        @Valid DeviceInfo deviceInfo
) {
    public record DeviceInfo(
            @NotBlank String deviceId,
            @NotBlank String userAgent,
            @NotBlank String ipAddress
    ) {
    }
}
