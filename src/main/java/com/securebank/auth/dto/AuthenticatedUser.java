package com.securebank.auth.dto;

import java.util.Set;

public record AuthenticatedUser(String id, String email, Set<String> roles) {
}
