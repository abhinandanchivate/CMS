package com.securebank.auth.dto;

import java.util.List;

public record MfaSetupResponse(
        String type,
        String secret,
        String qrImageDataUri,
        List<String> recoveryCodes
) {
}
