package com.securebank.auth.dto;

public record RefreshTokenResponse(String accessToken, long expiresIn) {
}
