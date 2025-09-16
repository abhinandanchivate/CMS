package com.securebank.profile.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String mobile,
        String address
) {
}
