package com.securebank.domain.card;

import com.securebank.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByUser(User user);

    Optional<Card> findByIdAndUser(UUID id, User user);
}
