package com.fullstackapp.rs3tracker.controller;

import com.fullstackapp.rs3tracker.dto.PlayerProfileResponse;
import com.fullstackapp.rs3tracker.dto.QuestData;
import com.fullstackapp.rs3tracker.dto.SkillValue;
import com.fullstackapp.rs3tracker.dto.SkillXpGained;
import com.fullstackapp.rs3tracker.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<PlayerProfileResponse> getFullProfile(@PathVariable String username) {
        return ResponseEntity.ok(playerService.getFullProfile(username));
    }

    @GetMapping("/{username}/quests")
    public ResponseEntity<List<QuestData>> getQuests(@PathVariable String username) {
        return ResponseEntity.ok(playerService.getQuests(username));
    }

    @GetMapping("/{username}/skills")
    public ResponseEntity<List<SkillValue>> getSkills(@PathVariable String username) {
        return ResponseEntity.ok(playerService.getSkills(username));
    }

    @GetMapping("/{username}/xp-gained")
    public ResponseEntity<List<SkillXpGained>> getXpGained(
            @PathVariable String username,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(playerService.getXpGained(username, days));
    }
}
