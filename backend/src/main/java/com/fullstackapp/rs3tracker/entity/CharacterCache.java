package com.fullstackapp.rs3tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "character_cache")
public class CharacterCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "last_fetched")
    private LocalDateTime lastFetched;

    @Column(name = "cached_response", columnDefinition = "TEXT")
    private String cachedResponse;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getLastFetched() { return lastFetched; }
    public void setLastFetched(LocalDateTime lastFetched) { this.lastFetched = lastFetched; }
    public String getCachedResponse() { return cachedResponse; }
    public void setCachedResponse(String cachedResponse) { this.cachedResponse = cachedResponse; }
}
