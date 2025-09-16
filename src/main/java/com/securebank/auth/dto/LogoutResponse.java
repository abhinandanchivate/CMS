package com.securebank.auth.dto;

public record LogoutResponse(String status) {

    public static LogoutResponse loggedOut() {
        return new LogoutResponse("logged_out");
    }
}
