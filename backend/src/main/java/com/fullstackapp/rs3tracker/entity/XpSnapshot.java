package com.fullstackapp.rs3tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "xp_snapshots")
public class XpSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(name = "skill_id", nullable = false)
    private int skillId;

    @Column(nullable = false)
    private long xp;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public XpSnapshot() {}

    public XpSnapshot(String username, int skillId, long xp, LocalDateTime recordedAt) {
        this.username = username;
        this.skillId = skillId;
        this.xp = xp;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    public long getXp() { return xp; }
    public void setXp(long xp) { this.xp = xp; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
