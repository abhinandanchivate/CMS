package com.securebank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "securebank.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @Positive @DefaultValue("3600") long accessTokenTtlSeconds,
        @Positive @DefaultValue("2592000") long refreshTokenTtlSeconds
) {
}
