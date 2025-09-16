package com.securebank.beneficiary.dto;

import java.util.List;

public record BeneficiaryListResponse(List<BeneficiaryDto> beneficiaries) {
}
