package com.securebank.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateTicketRequest(
        @NotBlank String category,
        @NotBlank String subject,
        @NotBlank String description,
        List<String> attachments
) {
}
