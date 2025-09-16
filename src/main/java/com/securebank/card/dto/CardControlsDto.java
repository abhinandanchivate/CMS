package com.securebank.card.dto;

public record CardControlsDto(
        boolean locked,
        boolean international,
        boolean ecom,
        Integer atmDailyLimit,
        Integer posDailyLimit
) {
}
