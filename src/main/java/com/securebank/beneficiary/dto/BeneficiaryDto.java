package com.securebank.beneficiary.dto;

public record BeneficiaryDto(
        String id,
        String name,
        String accountNumber,
        String ifsc,
        String bankName,
        boolean verified
) {
}
