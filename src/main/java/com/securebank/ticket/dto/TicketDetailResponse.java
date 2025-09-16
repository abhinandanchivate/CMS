package com.securebank.ticket.dto;

import java.util.List;

public record TicketDetailResponse(String ticketId, String status, List<TicketMessageDto> thread) {
}
