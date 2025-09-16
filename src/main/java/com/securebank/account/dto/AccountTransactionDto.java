package com.securebank.account.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountTransactionDto(
        String txnId,
        String type,
        String narration,
        BigDecimal amount,
        String currency,
        Instant postedAt,
        BigDecimal balanceAfter
) {
}
