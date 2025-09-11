package com.example.auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupResponse {
    private String qrCode;
    private String secret;
    private List<String> backupCodes;
}
