package com.example.auth.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String mfaCode;
    private DeviceInfo deviceInfo;

    @Data
    public static class DeviceInfo {
        private String deviceId;
        private String userAgent;
        private String ipAddress;
    }
}
