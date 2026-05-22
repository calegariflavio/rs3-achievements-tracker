package com.fullstackapp.rs3tracker.service;

import com.fullstackapp.rs3tracker.entity.Account;
import com.fullstackapp.rs3tracker.entity.CharacterClaim;
import com.fullstackapp.rs3tracker.exception.ConflictException;
import com.fullstackapp.rs3tracker.exception.ResourceNotFoundException;
import com.fullstackapp.rs3tracker.repository.AccountRepository;
import com.fullstackapp.rs3tracker.repository.CharacterClaimRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterOwnershipServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CharacterClaimRepository characterClaimRepository;

    @InjectMocks
    private CharacterOwnershipService service;

    // ── isOwner ───────────────────────────────────────────────────────────────

    @Test
    void isOwner_accountNotFound_returnsFalse() {
        when(accountRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThat(service.isOwner("ghost@test.com", "zezima")).isFalse();
        verifyNoInteractions(characterClaimRepository);
    }

    @Test
    void isOwner_claimFoundAndMatchesAccount_returnsTrue() {
        UUID accountId = UUID.randomUUID();
        Account account = buildAccount(accountId, "user@test.com");
        CharacterClaim claim = buildClaim(accountId, "zezima");

        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));
        when(characterClaimRepository.findByCharacterNameIgnoreCase("zezima"))
                .thenReturn(Optional.of(claim));

        assertThat(service.isOwner("user@test.com", "zezima")).isTrue();
    }

    @Test
    void isOwner_claimFoundButDifferentAccount_returnsFalse() {
        UUID accountId = UUID.randomUUID();
        UUID otherAccountId = UUID.randomUUID();
        Account account = buildAccount(accountId, "user@test.com");
        CharacterClaim claim = buildClaim(otherAccountId, "zezima");

        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));
        when(characterClaimRepository.findByCharacterNameIgnoreCase("zezima"))
                .thenReturn(Optional.of(claim));

        assertThat(service.isOwner("user@test.com", "zezima")).isFalse();
    }

    @Test
    void isOwner_claimNotFound_returnsFalse() {
        UUID accountId = UUID.randomUUID();
        Account account = buildAccount(accountId, "user@test.com");

        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));
        when(characterClaimRepository.findByCharacterNameIgnoreCase("unclaimed"))
                .thenReturn(Optional.empty());

        assertThat(service.isOwner("user@test.com", "unclaimed")).isFalse();
    }

    // ── claimCharacter ────────────────────────────────────────────────────────

    @Test
    void claimCharacter_characterAlreadyClaimed_throwsConflictException() {
        when(characterClaimRepository.existsByCharacterNameIgnoreCase("zezima")).thenReturn(true);

        assertThatThrownBy(() -> service.claimCharacter("user@test.com", "zezima"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("zezima");
    }

    @Test
    void claimCharacter_accountNotFound_throwsResourceNotFoundException() {
        when(characterClaimRepository.existsByCharacterNameIgnoreCase("zezima")).thenReturn(false);
        when(accountRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.claimCharacter("nobody@test.com", "zezima"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("nobody@test.com");
    }

    @Test
    void claimCharacter_success_savesAndReturnsClaim() {
        UUID accountId = UUID.randomUUID();
        Account account = buildAccount(accountId, "user@test.com");

        CharacterClaim saved = buildClaim(accountId, "zezima");
        when(characterClaimRepository.existsByCharacterNameIgnoreCase("zezima")).thenReturn(false);
        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));
        when(characterClaimRepository.save(any())).thenReturn(saved);

        CharacterClaim result = service.claimCharacter("user@test.com", "zezima");

        assertThat(result.getAccountId()).isEqualTo(accountId);
        assertThat(result.getCharacterName()).isEqualTo("zezima");
        verify(characterClaimRepository).save(any(CharacterClaim.class));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Account buildAccount(UUID id, String email) {
        Account a = new Account();
        a.setEmail(email);
        // Inject the id via reflection since there is no setter
        try {
            java.lang.reflect.Field f = Account.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(a, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return a;
    }

    private CharacterClaim buildClaim(UUID accountId, String characterName) {
        CharacterClaim c = new CharacterClaim();
        c.setAccountId(accountId);
        c.setCharacterName(characterName);
        return c;
    }
}
