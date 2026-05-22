package com.fullstackapp.rs3tracker.repository;

import com.fullstackapp.rs3tracker.entity.CharacterCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CharacterCacheRepository extends JpaRepository<CharacterCache, Long> {
    Optional<CharacterCache> findByUsernameIgnoreCase(String username);
}
