package com.fullstackapp.rs3tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstackapp.rs3tracker.dto.*;
import com.fullstackapp.rs3tracker.entity.CharacterCache;
import com.fullstackapp.rs3tracker.entity.XpSnapshot;
import com.fullstackapp.rs3tracker.exception.ExternalApiException;
import com.fullstackapp.rs3tracker.repository.CharacterCacheRepository;
import com.fullstackapp.rs3tracker.repository.XpSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    // RuneMetrics skill IDs 0-28 in order
    private static final String[] SKILL_NAMES = {
            "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer", "Magic",
            "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting",
            "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming",
            "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering",
            "Divination", "Invention", "Archaeology", "Necromancy"
    };

    private static final String PROFILE_URL =
            "https://apps.runescape.com/runemetrics/profile/profile?user={username}&activities=20";
    private static final String QUESTS_URL =
            "https://apps.runescape.com/runemetrics/quests?user={username}";
    private static final String HISCORES_URL =
            "https://secure.runescape.com/m=hiscore/index_lite.ws?player={username}";

    // Hiscores CSV: row 0 = Overall, rows 1-29 = skills, rows 30+ = activities/bosses.
    private static final String[] BOSS_NAMES = {
            "Bounty Hunter", "B.H. Rogues", "Dominion Tower", "The Crucible", "Castle Wars Games",
            "B.H. Collector", "BA Attackers", "BA Defenders", "BA Collectors", "BA Healers",
            "Duel Tournament", "Mobilising Armies", "Conquest", "Fist of Guthix", "GG Athletics",
            "GG Resource Race", "WE2 Armadyl Kills", "WE2 Bandos Kills", "WE2 Armadyl Scores",
            "WE2 Bandos Scores", "Heist Guard Level", "Heist Robber Level", "CFP 5 Dev",
            "AFK Theatre", "Runescore", "Clue Scrolls Easy", "Clue Scrolls Medium", "Clue Scrolls Hard",
            "Clue Scrolls Elite", "Clue Scrolls Master", "LMS Rank", "Soul Wars Zeal",
            "Abyssal Sire", "Cerberus", "Chaos Elemental", "Chaos Fanatic", "Commander Zilyana",
            "Corporeal Beast", "Crazy Archaeologist", "Dagannoth Prime", "Dagannoth Rex",
            "Dagannoth Supreme", "Deranged Archaeologist", "General Graardor", "Giant Mole",
            "Grotesque Guardians", "Hespori", "Kalphite Queen", "King Black Dragon", "Kraken",
            "Kree'arra", "K'ril Tsutsaroth", "Mimic", "Nightmare", "Phosani's Nightmare",
            "Nex", "Sarachnis", "Scorpia", "Skotizo", "Tempoross", "The Gauntlet", "The Corrupted Gauntlet",
            "Theatre of Blood", "Theatre of Blood Hard", "TzKal-Zuk", "TzTok-Jad", "Venenatis",
            "Vet'ion", "Vitur", "Zulrah"
    };

    private final RestTemplate restTemplate;
    private final CharacterCacheRepository cacheRepository;
    private final XpSnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper;

    public PlayerService(RestTemplate restTemplate,
                         CharacterCacheRepository cacheRepository,
                         XpSnapshotRepository snapshotRepository,
                         ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.cacheRepository = cacheRepository;
        this.snapshotRepository = snapshotRepository;
        this.objectMapper = objectMapper;
    }

    public PlayerProfileResponse getFullProfile(String username) {
        Optional<CharacterCache> cached = cacheRepository.findByUsernameIgnoreCase(username);
        if (cached.isPresent()) {
            CharacterCache entry = cached.get();
            if (entry.getLastFetched().isAfter(LocalDateTime.now().minus(CACHE_TTL))) {
                try {
                    return objectMapper.readValue(entry.getCachedResponse(), PlayerProfileResponse.class);
                } catch (Exception ignored) {
                    // cache corrupted — fall through to re-fetch
                }
            }
        }

        PlayerProfileResponse profile = fetchFromApis(username);
        saveCache(username, profile, cached);
        saveXpSnapshots(username, profile.skills());
        return profile;
    }

    public List<QuestData> getQuests(String username) {
        return getFullProfile(username).quests();
    }

    public List<SkillValue> getSkills(String username) {
        return getFullProfile(username).skills();
    }

    private PlayerProfileResponse fetchFromApis(String username) {
        RunemetricsProfile profile = fetchProfile(username);
        List<QuestData> quests = fetchQuests(username);
        List<HiscoreEntry> bossKills = fetchHiscores(username);

        // RuneMetrics returns xp as tenths (actual_xp * 10), so divide by 10
        List<SkillValue> skills = profile.skillValues != null
                ? profile.skillValues.stream()
                        .map(s -> new SkillValue(s.id(), s.level(), s.xp() / 10, s.rank()))
                        .sorted(Comparator.comparingInt(SkillValue::id))
                        .collect(java.util.stream.Collectors.toList())
                : Collections.emptyList();

        return new PlayerProfileResponse(
                profile.name,
                profile.combatLevel,
                profile.totalXp,
                profile.totalSkillLevel,
                profile.questsComplete,
                profile.questsStarted,
                profile.questsNotStarted,
                profile.rank,
                "true".equalsIgnoreCase(profile.loggedIn),
                skills,
                profile.activities != null ? profile.activities : Collections.emptyList(),
                quests,
                bossKills
        );
    }

    private RunemetricsProfile fetchProfile(String username) {
        String body;
        try {
            body = restTemplate.getForObject(PROFILE_URL, String.class, username);
        } catch (HttpClientErrorException e) {
            throw new ExternalApiException("Player not found: " + username, 404);
        } catch (Exception e) {
            throw new ExternalApiException("RuneMetrics API unavailable", 502);
        }

        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                String error = root.get("error").asText();
                if ("PROFILE_PRIVACY".equals(error)) {
                    throw new ExternalApiException("Profile is private: " + username, 403);
                }
                throw new ExternalApiException("Player not found: " + username, 404);
            }
            return objectMapper.treeToValue(root, RunemetricsProfile.class);
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalApiException("Failed to parse profile response", 502);
        }
    }

    private List<QuestData> fetchQuests(String username) {
        try {
            String body = restTemplate.getForObject(QUESTS_URL, String.class, username);
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                return Collections.emptyList();
            }
            RunemetricsQuestsResponse response = objectMapper.treeToValue(root, RunemetricsQuestsResponse.class);
            return response.quests != null ? response.quests : Collections.emptyList();
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<HiscoreEntry> fetchHiscores(String username) {
        try {
            String body = restTemplate.getForObject(HISCORES_URL, String.class, username);
            return parseHiscores(body);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<HiscoreEntry> parseHiscores(String body) {
        if (body == null || body.isBlank()) return Collections.emptyList();
        String[] lines = body.trim().split("\n");
        List<HiscoreEntry> bossKills = new ArrayList<>();
        // Rows 0-29: Overall + 29 skills — skip. Rows 30+ are activities/bosses.
        for (int i = 30; i < lines.length && (i - 30) < BOSS_NAMES.length; i++) {
            String[] parts = lines[i].trim().split(",");
            if (parts.length < 2) continue;
            try {
                long killCount = parts.length >= 3
                        ? Long.parseLong(parts[2].trim())
                        : Long.parseLong(parts[1].trim());
                if (killCount <= 0) continue;
                int rank = Integer.parseInt(parts[0].trim());
                bossKills.add(new HiscoreEntry(BOSS_NAMES[i - 30], rank, killCount));
            } catch (NumberFormatException ignored) {
            }
        }
        return bossKills;
    }

    public List<SkillXpGained> getXpGained(String username, int days) {
        int clampedDays = Math.max(1, Math.min(days, 365));
        LocalDateTime since = LocalDateTime.now().minusDays(clampedDays);

        List<XpSnapshot> snapshots = snapshotRepository
                .findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(username, since);

        if (snapshots.isEmpty()) return Collections.emptyList();

        // Group by skillId → sort dates → pick max XP per day
        Map<Integer, TreeMap<LocalDate, Long>> bySkill = new LinkedHashMap<>();
        for (XpSnapshot s : snapshots) {
            bySkill.computeIfAbsent(s.getSkillId(), k -> new TreeMap<>())
                   .merge(s.getRecordedAt().toLocalDate(), s.getXp(), Math::max);
        }

        List<SkillXpGained> result = new ArrayList<>();
        for (Map.Entry<Integer, TreeMap<LocalDate, Long>> entry : bySkill.entrySet()) {
            int skillId = entry.getKey();
            if (skillId < 0 || skillId >= SKILL_NAMES.length) continue;

            TreeMap<LocalDate, Long> dailyXp = entry.getValue();
            if (dailyXp.size() < 2) continue; // need at least 2 days to compute a gain

            List<XpDataPoint> dataPoints = dailyXp.entrySet().stream()
                    .map(e -> new XpDataPoint(e.getKey().toString(), e.getValue()))
                    .collect(Collectors.toList());

            long gained = Math.max(0, dailyXp.lastEntry().getValue() - dailyXp.firstEntry().getValue());
            if (gained == 0) continue;

            result.add(new SkillXpGained(skillId, SKILL_NAMES[skillId], dataPoints, gained));
        }

        result.sort(Comparator.comparingLong(SkillXpGained::xpGained).reversed());
        return result;
    }

    private void saveXpSnapshots(String username, List<SkillValue> skills) {
        if (skills == null || skills.isEmpty()) return;
        try {
            Optional<XpSnapshot> latest = snapshotRepository
                    .findFirstByUsernameIgnoreCaseOrderByRecordedAtDesc(username);
            if (latest.isPresent() && latest.get().getRecordedAt().isAfter(LocalDateTime.now().minusHours(1))) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            List<XpSnapshot> snapshots = skills.stream()
                    .map(s -> new XpSnapshot(username.toLowerCase(), s.id(), s.xp(), now))
                    .collect(Collectors.toList());
            snapshotRepository.saveAll(snapshots);
        } catch (Exception ignored) {
            // snapshot write failure is non-fatal
        }
    }

    private void saveCache(String username, PlayerProfileResponse profile, Optional<CharacterCache> existing) {
        try {
            CharacterCache cache = existing.orElse(new CharacterCache());
            cache.setUsername(username.toLowerCase());
            cache.setLastFetched(LocalDateTime.now());
            cache.setCachedResponse(objectMapper.writeValueAsString(profile));
            cacheRepository.save(cache);
        } catch (Exception ignored) {
            // cache write failure is non-fatal
        }
    }
}
