package com.securebank.domain.ticket;

import com.securebank.common.model.TicketStatus;
import com.securebank.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByUserAndStatusIn(User user, List<TicketStatus> statuses);

    List<Ticket> findByUser(User user);

    Optional<Ticket> findByIdAndUser(UUID id, User user);

    Optional<Ticket> findByReferenceAndUser(String reference, User user);
}
