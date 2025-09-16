package com.securebank.auth.dto;

public record RegistrationResponse(String status) {

    public static RegistrationResponse pendingVerification() {
        return new RegistrationResponse("pending_verification");
    }
}
