package com.example.auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse {
    private UUID id;
    private String email;
    private List<String> roles;
    private String firstName;
    private String lastName;
}
