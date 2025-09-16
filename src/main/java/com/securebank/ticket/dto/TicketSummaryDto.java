package com.securebank.ticket.dto;

import java.time.Instant;

public record TicketSummaryDto(String ticketId, String status, String subject, Instant updatedAt) {
}
