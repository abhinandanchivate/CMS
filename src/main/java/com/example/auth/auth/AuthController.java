package com.example.auth.auth;

import com.example.auth.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/password-reset")
    public PasswordResetResponse passwordReset(@Valid @RequestBody PasswordResetRequest request) {
        return authService.initiatePasswordReset(request);
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/mfa/setup")
    public MfaSetupResponse setupMfa(@RequestBody MfaSetupRequest request) {
        return authService.setupMfa(request);
    }

    @PostMapping("/device/register")
    public DeviceRegisterResponse registerDevice(@RequestBody DeviceRegisterRequest request) {
        return authService.registerDevice(request);
    }

    @PostMapping("/logout")
    public LogoutResponse logout(@RequestBody LogoutRequest request) {
        return authService.logout(request);
    }

    @GetMapping("/me")
    public MeResponse me() {
        return authService.me();
    }
}
