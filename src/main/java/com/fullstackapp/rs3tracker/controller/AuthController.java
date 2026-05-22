package com.fullstackapp.rs3tracker.controller;

import com.fullstackapp.rs3tracker.dto.*;
import com.fullstackapp.rs3tracker.entity.Account;
import com.fullstackapp.rs3tracker.entity.CharacterClaim;
import com.fullstackapp.rs3tracker.exception.ConflictException;
import com.fullstackapp.rs3tracker.repository.AccountRepository;
import com.fullstackapp.rs3tracker.repository.CharacterClaimRepository;
import com.fullstackapp.rs3tracker.security.JwtUtil;
import com.fullstackapp.rs3tracker.service.CharacterOwnershipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountRepository accountRepository;
    private final CharacterClaimRepository characterClaimRepository;
    private final CharacterOwnershipService characterOwnershipService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AccountRepository accountRepository,
                          CharacterClaimRepository characterClaimRepository,
                          CharacterOwnershipService characterOwnershipService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.characterClaimRepository = characterClaimRepository;
        this.characterOwnershipService = characterOwnershipService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered: " + request.email());
        }
        Account account = new Account();
        account.setEmail(request.email());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setCreatedAt(LocalDateTime.now());
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(jwtUtil.generateToken(request.email()), request.email()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
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
