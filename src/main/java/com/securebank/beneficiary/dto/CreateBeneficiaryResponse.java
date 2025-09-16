package com.securebank.beneficiary.dto;

public record CreateBeneficiaryResponse(String id, String status) {

    public static CreateBeneficiaryResponse pending(String id) {
        return new CreateBeneficiaryResponse(id, "verification_pending");
    }
}
