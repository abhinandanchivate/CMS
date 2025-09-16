package com.securebank.card;

import com.securebank.card.dto.CardListResponse;
import com.securebank.card.dto.UpdateCardControlsRequest;
import com.securebank.card.dto.UpdateCardControlsResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public CardListResponse listCards() {
        return cardService.listCards();
    }

    @PatchMapping("/{cardId}/controls")
    public UpdateCardControlsResponse updateControls(@PathVariable String cardId,
                                                     @Valid @RequestBody UpdateCardControlsRequest request) {
        return cardService.updateControls(cardId, request);
    }
}
