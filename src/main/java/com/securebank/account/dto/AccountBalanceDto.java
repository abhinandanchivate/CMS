package com.securebank.account.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountBalanceDto(String currency, BigDecimal available, Instant asOf) {
}
