package com.fullstackapp.rs3tracker.service;

import com.fullstackapp.rs3tracker.dto.ArtifactRequest;
import com.fullstackapp.rs3tracker.dto.ArtifactResponse;
import com.fullstackapp.rs3tracker.entity.ArchaeologyLog;
import com.fullstackapp.rs3tracker.exception.ResourceNotFoundException;
import com.fullstackapp.rs3tracker.repository.ArchaeologyLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArchaeologyLogService {

    private final ArchaeologyLogRepository repository;

    public ArchaeologyLogService(ArchaeologyLogRepository repository) {
        this.repository = repository;
    }

    public ArtifactResponse addArtifact(String username, ArtifactRequest request) {
        ArchaeologyLog log = new ArchaeologyLog();
        log.setUsername(username.toLowerCase());
        log.setArtifactName(request.artifactName());
        log.setCollectionName(request.collectionName());
        log.setDigSite(request.digSite());
        log.setCollectedAt(LocalDateTime.now());
        return toResponse(repository.save(log));
    }

    public List<String> getArtifacts(String username) {
        return repository.findByUsernameIgnoreCase(username)
                .stream()
                .map(ArchaeologyLog::getArtifactName)
                .toList();
    }

    @Transactional
    public void removeArtifact(String username, String artifactName) {
        repository.findByUsernameIgnoreCaseAndArtifactName(username, artifactName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artifact '" + artifactName + "' not found for player " + username));
        repository.deleteByUsernameIgnoreCaseAndArtifactName(username, artifactName);
    }

    private ArtifactResponse toResponse(ArchaeologyLog log) {
        return new ArtifactResponse(
                log.getId(),
                log.getUsername(),
                log.getArtifactName(),
                log.getCollectionName(),
                log.getDigSite(),
                log.getCollectedAt()
        );
    }
}
