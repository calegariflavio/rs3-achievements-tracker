package com.fullstackapp.rs3tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstackapp.rs3tracker.dto.*;
import com.fullstackapp.rs3tracker.entity.CharacterCache;
import com.fullstackapp.rs3tracker.exception.ExternalApiException;
import com.fullstackapp.rs3tracker.repository.CharacterCacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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

    private static final Executor XP_FETCH_EXECUTOR = Executors.newFixedThreadPool(29);

    private static final String PROFILE_URL =
            "https://apps.runescape.com/runemetrics/profile/profile?user={username}&activities=20";
    private static final String QUESTS_URL =
            "https://apps.runescape.com/runemetrics/quests?user={username}";
    private static final String HISCORES_URL =
            "https://secure.runescape.com/m=hiscore/index_lite.ws?player={username}";
    private static final String XP_MONTHLY_URL =
            "https://apps.runescape.com/runemetrics/xp-monthly?user={username}&skillid={skillId}";

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
    private final ObjectMapper objectMapper;

    public PlayerService(RestTemplate restTemplate,
                         CharacterCacheRepository cacheRepository,
                         ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.cacheRepository = cacheRepository;
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

    public List<SkillXpGained> getXpGained(String username) {
        List<CompletableFuture<SkillXpGained>> futures = new ArrayList<>();
        for (int id = 0; id < SKILL_NAMES.length; id++) {
            final int skillId = id;
            futures.add(CompletableFuture.supplyAsync(
                    () -> fetchSkillXpGained(username, skillId), XP_FETCH_EXECUTOR));
        }
        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(SkillXpGained::totalXp).reversed())
                .collect(Collectors.toList());
    }

    private SkillXpGained fetchSkillXpGained(String username, int skillId) {
        try {
            String body = restTemplate.getForObject(XP_MONTHLY_URL, String.class, username, skillId);
            if (body == null || body.isBlank()) return null;
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) return null;

            List<MonthlyXpPoint> monthly = new ArrayList<>();
            JsonNode monthArray = root.path("month");
            if (monthArray.isArray()) {
                for (JsonNode node : monthArray) {
                    int dateInt = node.path("date").asInt(0);
                    long xp = node.path("xp").asLong(0);
                    String dateStr = String.valueOf(dateInt);
                    String month = dateStr.length() >= 6 ? dateStr.substring(0, 6) : dateStr;
                    monthly.add(new MonthlyXpPoint(month, xp));
                }
            }

            monthly.sort(Comparator.comparing(MonthlyXpPoint::month));
            List<MonthlyXpPoint> last12 = monthly.size() > 12
                    ? new ArrayList<>(monthly.subList(monthly.size() - 12, monthly.size()))
                    : new ArrayList<>(monthly);
            long total = last12.stream().mapToLong(MonthlyXpPoint::xpGain).sum();
            if (total == 0) return null;
            return new SkillXpGained(skillId, SKILL_NAMES[skillId], last12, total);
        } catch (Exception ignored) {
            return null;
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
