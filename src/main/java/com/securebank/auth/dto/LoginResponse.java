package com.securebank.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        boolean mfaRequired,
        AuthenticatedUser user
) {
}
