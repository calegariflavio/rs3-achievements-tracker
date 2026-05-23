package com.fullstackapp.rs3tracker.repository;

import com.fullstackapp.rs3tracker.entity.XpSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface XpSnapshotRepository extends JpaRepository<XpSnapshot, Long> {

    Optional<XpSnapshot> findFirstByUsernameIgnoreCaseOrderByRecordedAtDesc(String username);

    List<XpSnapshot> findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(
            String username, LocalDateTime after);
}
