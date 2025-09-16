package com.securebank.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateBeneficiaryRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "\\d{9,18}") String accountNumber,
        @NotBlank @Pattern(regexp = "\\d{9,18}") String confirmAccountNumber,
        @NotBlank String ifsc,
        @NotBlank String bankName
) {
}
