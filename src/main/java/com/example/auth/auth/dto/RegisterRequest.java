package com.example.auth.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    private Profile profile;
    private boolean acceptTerms;
    private String verificationMethod;

    @Data
    public static class Profile {
        private String firstName;
        private String lastName;
    }
}
