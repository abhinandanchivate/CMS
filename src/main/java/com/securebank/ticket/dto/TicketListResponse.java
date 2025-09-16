package com.securebank.ticket.dto;

import java.util.List;

public record TicketListResponse(List<TicketSummaryDto> tickets) {
}
