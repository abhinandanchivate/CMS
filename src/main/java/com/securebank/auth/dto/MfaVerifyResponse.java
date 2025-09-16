package com.securebank.auth.dto;

public record MfaVerifyResponse(String status) {

    public static MfaVerifyResponse enabled() {
        return new MfaVerifyResponse("mfa_enabled");
    }
}
