package com.fullstackapp.rs3tracker.controller;

import com.fullstackapp.rs3tracker.dto.ArtifactRequest;
import com.fullstackapp.rs3tracker.dto.ArtifactResponse;
import com.fullstackapp.rs3tracker.dto.DeleteArtifactRequest;
import com.fullstackapp.rs3tracker.service.ArchaeologyLogService;
import com.fullstackapp.rs3tracker.service.CharacterOwnershipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/archaeology")
public class ArchaeologyLogController {

    private final ArchaeologyLogService archaeologyLogService;
    private final CharacterOwnershipService characterOwnershipService;

    public ArchaeologyLogController(ArchaeologyLogService archaeologyLogService,
                                    CharacterOwnershipService characterOwnershipService) {
        this.archaeologyLogService = archaeologyLogService;
        this.characterOwnershipService = characterOwnershipService;
    }

    @PostMapping("/{username}/artifact")
    public ResponseEntity<ArtifactResponse> addArtifact(
            @PathVariable String username,
            @RequestBody ArtifactRequest request) {
        verifyOwnership(username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(archaeologyLogService.addArtifact(username, request));
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<String>> getArtifacts(@PathVariable String username) {
        return ResponseEntity.ok(archaeologyLogService.getArtifacts(username));
    }

    @DeleteMapping("/{username}/artifact")
    public ResponseEntity<Void> removeArtifact(
            @PathVariable String username,
            @RequestBody DeleteArtifactRequest request) {
        verifyOwnership(username);
        archaeologyLogService.removeArtifact(username, request.artifactName());
        return ResponseEntity.noContent().build();
    }

    private void verifyOwnership(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : null;
        if (email == null || !characterOwnershipService.isOwner(email, username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not own character: " + username);
        }
    }
}
