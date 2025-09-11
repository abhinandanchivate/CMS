package com.example.auth.auth;

import com.example.auth.auth.dto.*;
import com.example.auth.security.JwtService;
import com.example.auth.token.RefreshToken;
import com.example.auth.token.RefreshTokenRepository;
import com.example.auth.user.User;
import com.example.auth.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getProfile() != null ? request.getProfile().getFirstName() : null)
                .lastName(request.getProfile() != null ? request.getProfile().getLastName() : null)
                .build();
        user.getRoles().add("patient");
        userRepository.save(user);
        return new RegisterResponse("pending_verification");
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(7 * 24 * 3600))
                .build();
        refreshTokenRepository.save(refreshToken);
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.getId(), List.copyOf(user.getRoles()));
        return new LoginResponse(accessToken, refreshTokenString, 3600, userInfo);
    }

    public PasswordResetResponse initiatePasswordReset(PasswordResetRequest request) {
        return new PasswordResetResponse("sent", UUID.randomUUID().toString(), 3600);
    }

    public RefreshResponse refresh(RefreshRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        String accessToken = jwtService.generateToken(token.getUser().getEmail());
        return new RefreshResponse(accessToken, 3600);
    }

    public MfaSetupResponse setupMfa(MfaSetupRequest request) {
        return new MfaSetupResponse("", UUID.randomUUID().toString(), List.of("12345678"));
    }

    public DeviceRegisterResponse registerDevice(DeviceRegisterRequest request) {
        return new DeviceRegisterResponse("verified");
    }

    public LogoutResponse logout(LogoutRequest request) {
        refreshTokenRepository.deleteByToken(request.getRefreshToken());
        return new LogoutResponse("revoked");
    }

    public MeResponse me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Not authenticated");
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return new MeResponse(user.getId(), user.getEmail(), List.copyOf(user.getRoles()), user.getFirstName(), user.getLastName());
    }
}
