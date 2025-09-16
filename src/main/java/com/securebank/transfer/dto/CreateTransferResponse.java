package com.securebank.transfer.dto;

import java.math.BigDecimal;

public record CreateTransferResponse(
        String transferId,
        String status,
        BigDecimal amount,
        Debit debit,
        Credit credit
) {
    public record Debit(String accountId) {
    }

    public record Credit(String beneficiaryId) {
    }
}
