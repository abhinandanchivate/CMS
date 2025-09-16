package com.securebank.auth.dto;

public record ResetPasswordResponse(String status) {

    public static ResetPasswordResponse updated() {
        return new ResetPasswordResponse("password_updated");
    }
}
