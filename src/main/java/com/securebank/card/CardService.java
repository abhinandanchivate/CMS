package com.securebank.card;

import com.securebank.card.dto.CardControlsDto;
import com.securebank.card.dto.CardDto;
import com.securebank.card.dto.CardListResponse;
import com.securebank.card.dto.UpdateCardControlsRequest;
import com.securebank.card.dto.UpdateCardControlsResponse;
import com.securebank.common.security.CurrentUserService;
import com.securebank.domain.card.Card;
import com.securebank.domain.card.CardRepository;
import com.securebank.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {

    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final CardRepository cardRepository;
    private final CurrentUserService currentUserService;

    public CardService(CardRepository cardRepository, CurrentUserService currentUserService) {
        this.cardRepository = cardRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public CardListResponse listCards() {
        User user = currentUserService.requireCurrentUser();
        List<CardDto> cards = cardRepository.findByUser(user).stream()
                .map(card -> new CardDto(
                        card.getId().toString(),
                        card.getMasked(),
                        card.getType().name(),
                        card.getExpires() != null ? card.getExpires().format(EXPIRY_FORMAT) : null,
                        new CardControlsDto(card.isLocked(), card.isInternational(), card.isEcommerce(),
                                card.getAtmDailyLimit(), card.getPosDailyLimit())
                ))
                .collect(Collectors.toList());
        return new CardListResponse(cards);
    }

    @Transactional
    public UpdateCardControlsResponse updateControls(String cardId, UpdateCardControlsRequest request) {
        User user = currentUserService.requireCurrentUser();
        Card card = cardRepository.findByIdAndUser(UUID.fromString(cardId), user)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        if (request.locked() != null) {
            card.setLocked(request.locked());
        }
        if (request.international() != null) {
            card.setInternational(request.international());
        }
        if (request.ecom() != null) {
            card.setEcommerce(request.ecom());
        }
        if (request.atmDailyLimit() != null) {
            card.setAtmDailyLimit(request.atmDailyLimit());
        }
        if (request.posDailyLimit() != null) {
            card.setPosDailyLimit(request.posDailyLimit());
        }
        cardRepository.save(card);
        return UpdateCardControlsResponse.updated();
    }
}
