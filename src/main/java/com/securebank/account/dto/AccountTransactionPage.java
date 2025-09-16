package com.securebank.account.dto;

import java.util.List;

public record AccountTransactionPage(
        List<AccountTransactionDto> content,
        int page,
        int size,
        long totalElements
) {
}
