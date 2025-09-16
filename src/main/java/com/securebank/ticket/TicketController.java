package com.securebank.ticket;

import com.securebank.ticket.dto.CreateTicketRequest;
import com.securebank.ticket.dto.CreateTicketResponse;
import com.securebank.ticket.dto.TicketDetailResponse;
import com.securebank.ticket.dto.TicketListResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<CreateTicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        CreateTicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public TicketListResponse listTickets(@RequestParam(value = "status", required = false) List<String> statuses) {
        return ticketService.listTickets(statuses);
    }

    @GetMapping("/{ticketId}")
    public TicketDetailResponse getTicket(@PathVariable String ticketId) {
        return ticketService.getTicket(ticketId);
    }
}
