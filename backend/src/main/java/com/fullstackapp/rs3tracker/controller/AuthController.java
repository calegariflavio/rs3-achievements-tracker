package com.fullstackapp.rs3tracker.controller;

import com.fullstackapp.rs3tracker.dto.*;
import com.fullstackapp.rs3tracker.entity.Account;
import com.fullstackapp.rs3tracker.entity.CharacterClaim;
import com.fullstackapp.rs3tracker.exception.ConflictException;
import com.fullstackapp.rs3tracker.repository.AccountRepository;
import com.fullstackapp.rs3tracker.repository.CharacterClaimRepository;
import com.fullstackapp.rs3tracker.security.JwtUtil;
import com.fullstackapp.rs3tracker.service.CharacterOwnershipService;
import com.fullstackapp.rs3tracker.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountRepository accountRepository;
    private final CharacterClaimRepository characterClaimRepository;
    private final CharacterOwnershipService characterOwnershipService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(AccountRepository accountRepository,
                          CharacterClaimRepository characterClaimRepository,
                          CharacterOwnershipService characterOwnershipService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.accountRepository = accountRepository;
        this.characterClaimRepository = characterClaimRepository;
        this.characterOwnershipService = characterOwnershipService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered: " + request.email());
        }
        String token = UUID.randomUUID().toString().replace("-", "");

        Account account = new Account();
        account.setEmail(request.email());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setCreatedAt(LocalDateTime.now());
        account.setVerified(false);
        account.setVerificationToken(token);
        account.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
        accountRepository.save(account);

        emailService.sendVerificationEmail(request.email(), token);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new RegisterResponse("Registration successful. Please check your email to verify your account."));
    }

    @GetMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@RequestParam String token) {
        Account account = accountRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid or expired verification link."));

        if (account.isVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is already verified.");
        }
        if (account.getTokenExpiresAt() == null || account.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Verification link has expired. Please register again.");
        }

        account.setVerified(true);
        account.setVerificationToken(null);
        account.setTokenExpiresAt(null);
        accountRepository.save(account);

        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(account.getEmail()), account.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        if (!account.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Please verify your email before signing in.");
        }
        return ResponseEntity.ok(
                new AuthResponse(jwtUtil.generateToken(request.email()), request.email()));
    }

    @PostMapping("/claim")
    public ResponseEntity<ClaimResponse> claim(@Valid @RequestBody ClaimCharacterRequest request,
                                               Authentication authentication) {
        requireAuthentication(authentication);
        CharacterClaim claim = characterOwnershipService.claimCharacter(
                authentication.getName(), request.characterName().toLowerCase());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ClaimResponse(claim.getCharacterName(), claim.getClaimedAt()));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        requireAuthentication(authentication);
        String email = authentication.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
        List<String> characters = characterClaimRepository.findByAccountId(account.getId())
                .stream()
                .map(claim -> claim.getCharacterName().toLowerCase())
                .toList();
        return ResponseEntity.ok(new MeResponse(account.getEmail(), account.getCreatedAt(), characters));
    }

    private void requireAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
    }

    public record ClaimResponse(String characterName, LocalDateTime claimedAt) {}
}
