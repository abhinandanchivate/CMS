package com.securebank.auth.dto;

import java.time.LocalDate;
import java.util.Set;

public record MeResponse(
        String id,
        String email,
        Set<String> roles,
        Profile profile
) {
    public record Profile(String firstName, String lastName, String mobile, LocalDate dob, String address) {
    }
}
