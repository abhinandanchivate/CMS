package com.example.auth.auth.dto;

import lombok.Data;

@Data
public class DeviceRegisterRequest {
    private String deviceId;
    private String otp;
}
