package com.example.auth.auth.dto;

import lombok.Data;

@Data
public class MfaSetupRequest {
    private String method;
    private String deviceName;
}
