package com.example.auth.auth.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
