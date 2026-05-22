package com.fullstackapp.rs3tracker.repository;

import com.fullstackapp.rs3tracker.entity.CharacterClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CharacterClaimRepository extends JpaRepository<CharacterClaim, UUID> {
    List<CharacterClaim> findByAccountId(UUID accountId);
    Optional<CharacterClaim> findByCharacterNameIgnoreCase(String characterName);
    boolean existsByCharacterNameIgnoreCase(String characterName);
}
