package com.securebank.account.dto;

public record AccountSummaryDto(
        String accountId,
        String type,
        String mask,
        String ifsc,
        String branch,
        AccountBalanceDto balance
) {
}
