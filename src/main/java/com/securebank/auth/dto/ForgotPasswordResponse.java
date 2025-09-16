package com.securebank.auth.dto;

public record ForgotPasswordResponse(String status) {

    public static ForgotPasswordResponse otpSent() {
        return new ForgotPasswordResponse("otp_sent");
    }
}
