package com.securebank.account.dto;

import java.util.List;

public record AccountListResponse(List<AccountSummaryDto> accounts) {
}
