package com.securebank.ticket.dto;

import java.time.Instant;

public record TicketMessageDto(String by, Instant at, String message) {
}
