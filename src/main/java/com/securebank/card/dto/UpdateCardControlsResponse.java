package com.securebank.card.dto;

public record UpdateCardControlsResponse(String status) {

    public static UpdateCardControlsResponse updated() {
        return new UpdateCardControlsResponse("updated");
    }
}
