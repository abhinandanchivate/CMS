package com.securebank.profile.dto;

import java.time.LocalDate;

public record ProfileResponse(
        String id,
        String email,
        String firstName,
        String lastName,
        String mobile,
        LocalDate dob,
        String address
) {
}
