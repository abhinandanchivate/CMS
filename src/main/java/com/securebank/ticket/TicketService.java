package com.securebank.ticket;

import com.securebank.common.model.TicketActor;
import com.securebank.common.model.TicketCategory;
import com.securebank.common.model.TicketStatus;
import com.securebank.common.security.CurrentUserService;
import com.securebank.domain.ticket.Ticket;
import com.securebank.domain.ticket.TicketMessage;
import com.securebank.domain.ticket.TicketMessageRepository;
import com.securebank.domain.ticket.TicketRepository;
import com.securebank.domain.user.User;
import com.securebank.ticket.dto.CreateTicketRequest;
import com.securebank.ticket.dto.CreateTicketResponse;
import com.securebank.ticket.dto.TicketDetailResponse;
import com.securebank.ticket.dto.TicketListResponse;
import com.securebank.ticket.dto.TicketMessageDto;
import com.securebank.ticket.dto.TicketSummaryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private static final DateTimeFormatter TICKET_REFERENCE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            .withZone(ZoneOffset.UTC);

    private final TicketRepository ticketRepository;
    private final TicketMessageRepository ticketMessageRepository;
    private final CurrentUserService currentUserService;

    public TicketService(TicketRepository ticketRepository,
                         TicketMessageRepository ticketMessageRepository,
                         CurrentUserService currentUserService) {
        this.ticketRepository = ticketRepository;
        this.ticketMessageRepository = ticketMessageRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public CreateTicketResponse createTicket(CreateTicketRequest request) {
        User user = currentUserService.requireCurrentUser();
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setReference(generateReference());
        ticket.setCategory(TicketCategory.valueOf(request.category().toUpperCase()));
        ticket.setSubject(request.subject());
        ticket.setStatus(TicketStatus.OPEN);
        Ticket saved = ticketRepository.save(ticket);

        TicketMessage message = new TicketMessage();
        message.setTicket(saved);
        message.setByRole(TicketActor.CUSTOMER);
        message.setMessage(request.description());
        ticketMessageRepository.save(message);

        return CreateTicketResponse.opened(saved.getReference());
    }

    @Transactional(readOnly = true)
    public TicketListResponse listTickets(List<String> statuses) {
        User user = currentUserService.requireCurrentUser();
        List<Ticket> tickets;
        if (statuses != null && !statuses.isEmpty()) {
            List<TicketStatus> statusEnums = statuses.stream()
                    .map(status -> TicketStatus.valueOf(status.toUpperCase()))
                    .toList();
            tickets = ticketRepository.findByUserAndStatusIn(user, statusEnums);
        } else {
            tickets = ticketRepository.findByUser(user);
        }
        List<TicketSummaryDto> summaries = tickets.stream()
                .map(ticket -> new TicketSummaryDto(
                        ticket.getReference(),
                        ticket.getStatus().name().toLowerCase(),
                        ticket.getSubject(),
                        ticket.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        return new TicketListResponse(summaries);
    }

    @Transactional(readOnly = true)
    public TicketDetailResponse getTicket(String ticketId) {
        User user = currentUserService.requireCurrentUser();
        Ticket ticket = resolveTicket(ticketId, user);
        List<TicketMessageDto> thread = ticketMessageRepository.findByTicketOrderByCreatedAtAsc(ticket).stream()
                .map(message -> new TicketMessageDto(
                        message.getByRole().name(),
                        message.getCreatedAt(),
                        message.getMessage()
                ))
                .collect(Collectors.toList());
        return new TicketDetailResponse(ticket.getReference(), ticket.getStatus().name().toLowerCase(), thread);
    }

    private Ticket resolveTicket(String ticketId, User user) {
        Optional<Ticket> byReference = ticketRepository.findByReferenceAndUser(ticketId, user);
        if (byReference.isPresent()) {
            return byReference.get();
        }
        try {
            UUID uuid = UUID.fromString(ticketId);
            return ticketRepository.findByIdAndUser(uuid, user)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Ticket not found");
        }
    }

    private String generateReference() {
        String timestamp = TICKET_REFERENCE_FORMAT.format(Instant.now());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "TKT-" + timestamp + "-" + suffix;
    }
}
