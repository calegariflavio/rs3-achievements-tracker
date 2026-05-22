package com.fullstackapp.rs3tracker.service;

import com.fullstackapp.rs3tracker.dto.ArtifactRequest;
import com.fullstackapp.rs3tracker.dto.ArtifactResponse;
import com.fullstackapp.rs3tracker.entity.ArchaeologyLog;
import com.fullstackapp.rs3tracker.exception.ResourceNotFoundException;
import com.fullstackapp.rs3tracker.repository.ArchaeologyLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchaeologyLogServiceTest {

    @Mock
    private ArchaeologyLogRepository repository;

    @InjectMocks
    private ArchaeologyLogService service;

    @Test
    void addArtifact_savesAndReturnsResponse() {
        ArtifactRequest request = new ArtifactRequest("Xeric's talisman", "Xeric's artefacts", "Quidamortem");

        ArchaeologyLog saved = new ArchaeologyLog();
        saved.setUsername("zezima");
        saved.setArtifactName("Xeric's talisman");
        saved.setCollectionName("Xeric's artefacts");
        saved.setDigSite("Quidamortem");
        saved.setCollectedAt(LocalDateTime.of(2024, 6, 1, 12, 0));

        when(repository.save(any())).thenReturn(saved);

        ArtifactResponse response = service.addArtifact("Zezima", request);

        assertThat(response.username()).isEqualTo("zezima");
        assertThat(response.artifactName()).isEqualTo("Xeric's talisman");
        assertThat(response.collectionName()).isEqualTo("Xeric's artefacts");
        assertThat(response.digSite()).isEqualTo("Quidamortem");
        verify(repository).save(any(ArchaeologyLog.class));
    }

    @Test
    void getArtifacts_returnsArtifactNames() {
        ArchaeologyLog log1 = new ArchaeologyLog();
        log1.setArtifactName("Sword hilt");
        ArchaeologyLog log2 = new ArchaeologyLog();
        log2.setArtifactName("Bronze statuette");

        when(repository.findByUsernameIgnoreCase("zezima")).thenReturn(List.of(log1, log2));

        List<String> result = service.getArtifacts("zezima");

        assertThat(result).containsExactly("Sword hilt", "Bronze statuette");
    }

    @Test
    void getArtifacts_emptyList_returnsEmpty() {
        when(repository.findByUsernameIgnoreCase("unknown")).thenReturn(List.of());

        assertThat(service.getArtifacts("unknown")).isEmpty();
    }

    @Test
    void removeArtifact_whenFound_deletesEntry() {
        ArchaeologyLog log = new ArchaeologyLog();
        log.setArtifactName("Sword hilt");
        when(repository.findByUsernameIgnoreCaseAndArtifactName("zezima", "Sword hilt"))
                .thenReturn(Optional.of(log));

        service.removeArtifact("zezima", "Sword hilt");

        verify(repository).deleteByUsernameIgnoreCaseAndArtifactName("zezima", "Sword hilt");
    }

    @Test
    void removeArtifact_whenNotFound_throwsResourceNotFoundException() {
        when(repository.findByUsernameIgnoreCaseAndArtifactName("zezima", "Missing artifact"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeArtifact("zezima", "Missing artifact"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Missing artifact")
                .hasMessageContaining("zezima");
    }
}
