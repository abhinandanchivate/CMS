package com.securebank.card.dto;

public record UpdateCardControlsRequest(Boolean locked, Boolean international, Boolean ecom, Integer atmDailyLimit, Integer posDailyLimit) {
}
