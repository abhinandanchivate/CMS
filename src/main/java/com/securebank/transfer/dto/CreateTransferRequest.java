package com.securebank.transfer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateTransferRequest(
        @NotBlank String fromAccountId,
        @Valid To to,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
        String purpose,
        @NotBlank @Pattern(regexp = "\\d{4,6}") String userPin
) {
    public record To(@NotBlank String beneficiaryId) {
    }
}
