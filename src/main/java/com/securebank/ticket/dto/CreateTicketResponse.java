package com.securebank.ticket.dto;

public record CreateTicketResponse(String ticketId, String status) {

    public static CreateTicketResponse opened(String ticketId) {
        return new CreateTicketResponse(ticketId, "open");
    }
}
