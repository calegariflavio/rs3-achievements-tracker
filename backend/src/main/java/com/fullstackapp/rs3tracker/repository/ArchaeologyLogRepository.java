package com.fullstackapp.rs3tracker.repository;

import com.fullstackapp.rs3tracker.entity.ArchaeologyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArchaeologyLogRepository extends JpaRepository<ArchaeologyLog, Long> {
    List<ArchaeologyLog> findByUsernameIgnoreCase(String username);
    Optional<ArchaeologyLog> findByUsernameIgnoreCaseAndArtifactName(String username, String artifactName);
    void deleteByUsernameIgnoreCaseAndArtifactName(String username, String artifactName);
}
