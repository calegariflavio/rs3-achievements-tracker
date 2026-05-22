package com.fullstackapp.rs3tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archaeology_log")
public class ArchaeologyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "artifact_name", nullable = false)
    private String artifactName;

    @Column(name = "collection_name")
    private String collectionName;

    @Column(name = "dig_site")
    private String digSite;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getArtifactName() { return artifactName; }
    public void setArtifactName(String artifactName) { this.artifactName = artifactName; }
    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
    public String getDigSite() { return digSite; }
    public void setDigSite(String digSite) { this.digSite = digSite; }
    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }
}
