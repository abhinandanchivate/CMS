package com.securebank.domain.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, UUID> {

    List<TicketMessage> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}
