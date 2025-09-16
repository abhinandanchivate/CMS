package com.securebank.card.dto;

public record CardDto(
        String cardId,
        String masked,
        String type,
        String expires,
        CardControlsDto controls
) {
}
