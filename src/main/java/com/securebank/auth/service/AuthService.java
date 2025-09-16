package com.securebank.auth.service;

import com.securebank.auth.dto.AuthenticatedUser;
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
import com.securebank.common.model.RoleType;
import com.securebank.common.security.CurrentUserService;
import com.securebank.config.JwtProperties;
import com.securebank.config.JwtService;
import com.securebank.domain.token.RefreshToken;
import com.securebank.domain.token.RefreshTokenRepository;
import com.securebank.domain.user.Role;
import com.securebank.domain.user.RoleRepository;
import com.securebank.domain.user.User;
import com.securebank.domain.user.UserPrincipal;
import com.securebank.domain.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final CurrentUserService currentUserService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       JwtProperties jwtProperties,
                       CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public RegistrationResponse register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (!Boolean.TRUE.equals(request.acceptTerms())) {
            throw new IllegalArgumentException("Terms must be accepted");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.profile().firstName());
        user.setLastName(request.profile().lastName());
        user.setMobile(request.profile().mobile());
        user.setEnabled(false);
        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.CUSTOMER)));
        user.addRole(customerRole);
        userRepository.save(user);
        return RegistrationResponse.pendingVerification();
    }

    @Transactional
    public VerificationResponse verify(VerificationRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        return VerificationResponse.verified();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();
        if (user.isMfaEnabled() && (request.mfaCode() == null || request.mfaCode().isBlank())) {
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                    user.getId().toString(),
                    user.getEmail(),
                    user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));
            return new LoginResponse(null, null, 0, true, authenticatedUser);
        }
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(refreshTokenValue, user,
                Instant.now().plusSeconds(jwtProperties.refreshTokenTtlSeconds()));
        refreshTokenRepository.save(refreshToken);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.getId().toString(),
                user.getEmail(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));
        return new LoginResponse(accessToken, refreshTokenValue, jwtProperties.accessTokenTtlSeconds(), false, authenticatedUser);
    }

    @Transactional(readOnly = true)
    public MeResponse me() {
        User user = currentUserService.requireCurrentUser();
        return new MeResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
                new MeResponse.Profile(user.getFirstName(), user.getLastName(), user.getMobile(), user.getDateOfBirth(), user.getAddress())
        );
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new IllegalArgumentException("Refresh token expired");
        }
        User user = refreshToken.getUser();
        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);
        return new RefreshTokenResponse(accessToken, jwtProperties.accessTokenTtlSeconds());
    }

    @Transactional
    public LogoutResponse logout(LogoutRequest request) {
        refreshTokenRepository.findByTokenAndRevokedFalse(request.refreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
        return LogoutResponse.loggedOut();
    }

    public MfaSetupResponse mfaSetup() {
        List<String> recoveryCodes = List.of("AB12-CD34", "EF56-GH78", "JK90-LM12");
        return new MfaSetupResponse("totp", "JBSWY3DPEHPK3PXP", "data:image/png;base64,iVBORw0KGgo...", recoveryCodes);
    }

    @Transactional
    public MfaVerifyResponse mfaVerify(MfaVerifyRequest request) {
        User user = currentUserService.requireCurrentUser();
        user.setMfaEnabled(true);
        userRepository.save(user);
        return MfaVerifyResponse.enabled();
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ForgotPasswordResponse.otpSent();
    }

    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return ResetPasswordResponse.updated();
    }
}
