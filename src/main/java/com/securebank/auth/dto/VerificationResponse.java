package com.securebank.auth.dto;

public record VerificationResponse(String status) {

    public static VerificationResponse verified() {
        return new VerificationResponse("verified");
    }
}
