package com.securebank.auth.controller;

import com.securebank.auth.dto.ForgotPasswordRequest;
import com.securebank.auth.dto.ForgotPasswordResponse;
import com.securebank.auth.dto.LoginRequest;
import com.securebank.auth.dto.LoginResponse;
import com.securebank.auth.dto.LogoutRequest;
import com.securebank.auth.dto.LogoutResponse;
import com.securebank.auth.dto.MfaSetupResponse;
import com.securebank.auth.dto.MfaVerifyRequest;
import com.securebank.auth.dto.MfaVerifyResponse;
import com.securebank.auth.dto.MeResponse;
import com.securebank.auth.dto.RefreshTokenRequest;
import com.securebank.auth.dto.RefreshTokenResponse;
import com.securebank.auth.dto.RegisterRequest;
import com.securebank.auth.dto.RegistrationResponse;
import com.securebank.auth.dto.ResetPasswordRequest;
import com.securebank.auth.dto.ResetPasswordResponse;
import com.securebank.auth.dto.VerificationRequest;
import com.securebank.auth.dto.VerificationResponse;
import com.securebank.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegistrationResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/verify")
    public VerificationResponse verify(@Valid @RequestBody VerificationRequest request) {
        return authService.verify(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/mfa/setup")
    public MfaSetupResponse setupMfa() {
        return authService.mfaSetup();
    }

    @PostMapping("/mfa/verify")
    public MfaVerifyResponse verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
        return authService.mfaVerify(request);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/me")
    public MeResponse me() {
        return authService.me();
    }

    @PostMapping("/logout")
    public LogoutResponse logout(@Valid @RequestBody LogoutRequest request) {
        return authService.logout(request);
    }

    @PostMapping("/password/forgot")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/password/reset")
    public ResetPasswordResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
}
