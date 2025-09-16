package com.securebank.profile.dto;

public record UpdateProfileResponse(String status) {

    public static UpdateProfileResponse updated() {
        return new UpdateProfileResponse("updated");
    }
}
