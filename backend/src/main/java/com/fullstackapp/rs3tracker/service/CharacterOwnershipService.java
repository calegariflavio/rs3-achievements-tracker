package com.fullstackapp.rs3tracker.service;

import com.fullstackapp.rs3tracker.entity.Account;
import com.fullstackapp.rs3tracker.entity.CharacterClaim;
import com.fullstackapp.rs3tracker.exception.ConflictException;
import com.fullstackapp.rs3tracker.exception.ResourceNotFoundException;
import com.fullstackapp.rs3tracker.repository.AccountRepository;
import com.fullstackapp.rs3tracker.repository.CharacterClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CharacterOwnershipService {

    private static final Logger log = LoggerFactory.getLogger(CharacterOwnershipService.class);

    private final AccountRepository accountRepository;
    private final CharacterClaimRepository characterClaimRepository;

    public CharacterOwnershipService(AccountRepository accountRepository,
                                     CharacterClaimRepository characterClaimRepository) {
        this.accountRepository = accountRepository;
        this.characterClaimRepository = characterClaimRepository;
    }

    public boolean isOwner(String email, String characterName) {
        Account account = accountRepository.findByEmail(email).orElse(null);
        if (account == null) {
            log.debug("isOwner: no account found for email={}, characterName={}, result=false",
                    email, characterName);
            return false;
        }
        boolean result = characterClaimRepository
                .findByCharacterNameIgnoreCase(characterName)
                .map(claim -> claim.getAccountId().equals(account.getId()))
                .orElse(false);
        log.debug("isOwner: email={}, characterName={}, accountId={}, result={}",
                email, characterName, account.getId(), result);
        return result;
    }

    @Transactional
    public CharacterClaim claimCharacter(String email, String characterName) {
        if (characterClaimRepository.existsByCharacterNameIgnoreCase(characterName)) {
            throw new ConflictException("Character '" + characterName + "' is already claimed");
        }
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + email));
        CharacterClaim claim = new CharacterClaim();
        claim.setAccountId(account.getId());
        claim.setCharacterName(characterName.toLowerCase());
        claim.setClaimedAt(LocalDateTime.now());
        return characterClaimRepository.save(claim);
    }
}
