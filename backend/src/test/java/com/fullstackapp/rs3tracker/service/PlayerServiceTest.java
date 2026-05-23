package com.fullstackapp.rs3tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstackapp.rs3tracker.dto.*;
import com.fullstackapp.rs3tracker.entity.CharacterCache;
import com.fullstackapp.rs3tracker.entity.XpSnapshot;
import com.fullstackapp.rs3tracker.exception.ExternalApiException;
import com.fullstackapp.rs3tracker.repository.CharacterCacheRepository;
import com.fullstackapp.rs3tracker.repository.XpSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CharacterCacheRepository cacheRepository;

    @Mock
    private XpSnapshotRepository snapshotRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PlayerService service;

    // Minimal valid RuneMetrics profile JSON
    private static final String PROFILE_JSON = """
            {
              "name": "Zezima",
              "combatlevel": 138,
              "totalxp": 5000000,
              "totalskill": 2595,
              "questscomplete": 300,
              "questsstarted": 10,
              "questsnotstarted": 5,
              "rank": "1",
              "loggedIn": "false",
              "skillvalues": [
                {"id": 0, "level": 99, "xp": 130000000, "rank": 1},
                {"id": 1, "level": 99, "xp": 130000000, "rank": 1}
              ],
              "activities": [
                {"date": "2024-01-01", "details": "Gained a level", "text": "Attack"}
              ]
            }
            """;

    private static final String QUESTS_JSON = """
            {
              "quests": [
                {"title":"Cook's Assistant","status":"COMPLETED","difficulty":0,"members":false,"questPoints":1,"userEligible":true}
              ]
            }
            """;

    // Hiscores CSV: 30+ rows. Rows 0-29 are skills, rows 30+ are bosses.
    private static final String HISCORES_CSV = buildHiscoresCsv();

    @BeforeEach
    void setup() {
        service = new PlayerService(restTemplate, cacheRepository, snapshotRepository, objectMapper);
    }

    // ── getFullProfile ────────────────────────────────────────────────────────

    @Test
    void getFullProfile_cacheHitAndFresh_returnsFromCache() throws Exception {
        PlayerProfileResponse cached = buildProfile("Zezima");
        CharacterCache entry = new CharacterCache();
        entry.setUsername("zezima");
        entry.setLastFetched(LocalDateTime.now());
        entry.setCachedResponse(objectMapper.writeValueAsString(cached));

        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.of(entry));

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.username()).isEqualTo("Zezima");
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getFullProfile_cacheHitButExpired_fetchesFromApi() throws Exception {
        PlayerProfileResponse cached = buildProfile("Zezima");
        CharacterCache entry = new CharacterCache();
        entry.setUsername("zezima");
        entry.setLastFetched(LocalDateTime.now().minusMinutes(10)); // expired
        entry.setCachedResponse(objectMapper.writeValueAsString(cached));

        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.of(entry));
        stubApiCalls();

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.username()).isEqualTo("Zezima");
        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(String.class), anyString());
        verify(cacheRepository).save(any(CharacterCache.class));
    }

    @Test
    void getFullProfile_cacheHitButCorruptedJson_fetchesFromApi() {
        CharacterCache entry = new CharacterCache();
        entry.setUsername("zezima");
        entry.setLastFetched(LocalDateTime.now());
        entry.setCachedResponse("this is not valid json {{{{");

        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.of(entry));
        stubApiCalls();

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.username()).isEqualTo("Zezima");
        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(String.class), anyString());
    }

    @Test
    void getFullProfile_noCache_fetchesFromApi() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        stubApiCalls();

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.username()).isEqualTo("Zezima");
        verify(cacheRepository).save(any(CharacterCache.class));
    }

    // ── getQuests / getSkills ─────────────────────────────────────────────────

    @Test
    void getQuests_returnsQuestList() throws Exception {
        PlayerProfileResponse cached = buildProfile("Zezima");
        CharacterCache entry = freshCache(cached);
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.of(entry));

        List<QuestData> quests = service.getQuests("Zezima");

        assertThat(quests).isNotNull();
    }

    @Test
    void getSkills_returnsSkillList() throws Exception {
        PlayerProfileResponse cached = buildProfile("Zezima");
        CharacterCache entry = freshCache(cached);
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.of(entry));

        List<SkillValue> skills = service.getSkills("Zezima");

        assertThat(skills).isNotNull();
    }

    // ── fetchProfile error paths ──────────────────────────────────────────────

    @Test
    void fetchProfile_httpClientError_throwsExternalApiException404() {
        when(cacheRepository.findByUsernameIgnoreCase("unknown")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertThatThrownBy(() -> service.getFullProfile("unknown"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void fetchProfile_genericRestException_throwsExternalApiException502() {
        when(cacheRepository.findByUsernameIgnoreCase("flaky")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> service.getFullProfile("flaky"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("unavailable");
    }

    @Test
    void fetchProfile_profilePrivacyError_throwsExternalApiException403() {
        when(cacheRepository.findByUsernameIgnoreCase("private")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn("{\"error\":\"PROFILE_PRIVACY\"}");

        assertThatThrownBy(() -> service.getFullProfile("private"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getStatusCode())
                .isEqualTo(403);
    }

    @Test
    void fetchProfile_otherJsonError_throwsExternalApiException404() {
        when(cacheRepository.findByUsernameIgnoreCase("missing")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn("{\"error\":\"NO_PROFILE\"}");

        assertThatThrownBy(() -> service.getFullProfile("missing"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getStatusCode())
                .isEqualTo(404);
    }

    @Test
    void fetchProfile_unparseableResponse_throwsExternalApiException502() {
        when(cacheRepository.findByUsernameIgnoreCase("bad")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn("not json at all !!!!");

        assertThatThrownBy(() -> service.getFullProfile("bad"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("parse");
    }

    // ── fetchQuests edge cases ────────────────────────────────────────────────

    @Test
    void fetchQuests_withErrorInResponse_returnsEmptyList() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn("{\"error\":\"PROFILE_PRIVATE\"}");
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(HISCORES_CSV);

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.quests()).isEmpty();
    }

    @Test
    void fetchQuests_withNullQuestsField_returnsEmptyList() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn("{\"quests\":null}");
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(HISCORES_CSV);

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.quests()).isEmpty();
    }

    @Test
    void fetchQuests_withException_returnsEmptyList() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenThrow(new RestClientException("quests API down"));
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(HISCORES_CSV);

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.quests()).isEmpty();
    }

    // ── fetchHiscores / parseHiscores ─────────────────────────────────────────

    @Test
    void fetchHiscores_withException_returnsEmptyBossKills() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenThrow(new RestClientException("hiscores down"));

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.bossKills()).isEmpty();
    }

    @Test
    void parseHiscores_withNullBody_returnsEmpty() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(null);

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.bossKills()).isEmpty();
    }

    @Test
    void parseHiscores_withBlankBody_returnsEmpty() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn("   ");

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.bossKills()).isEmpty();
    }

    @Test
    void parseHiscores_withBossKillsAndZeroKillCount_skipsZeroEntries() {
        // Build a CSV where some boss rows have kill count <= 0
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(buildHiscoresCsvWithZeroKills());

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        // All boss entries have kill count = 0, so should be empty
        assertThat(result.bossKills()).isEmpty();
    }

    @Test
    void parseHiscores_withShortLine_skipsEntry() {
        // Build CSV where some boss row has only 1 field
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(buildHiscoresCsvWithShortBossLine());

        // Should not throw
        service.getFullProfile("Zezima");
    }

    @Test
    void parseHiscores_withNonNumericLine_skipsEntry() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(buildHiscoresCsvWithNonNumericBossLine());

        // Should not throw
        service.getFullProfile("Zezima");
    }

    @Test
    void parseHiscores_withValidBossKills_includesThem() {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(HISCORES_CSV);

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        // HISCORES_CSV has a boss entry at row 30 with killCount > 0
        assertThat(result.bossKills()).isNotEmpty();
        assertThat(result.bossKills().get(0).killCount()).isGreaterThan(0);
    }

    // ── fetchFromApis — skill ordering ───────────────────────────────────────

    @Test
    void fetchFromApis_skillValuesReturnedOutOfOrder_areSortedById() {
        String profileWithReversedSkills = """
                {
                  "name": "ZombieW",
                  "combatlevel": 138,
                  "totalxp": 5000000,
                  "totalskill": 2595,
                  "questscomplete": 300,
                  "questsstarted": 10,
                  "questsnotstarted": 5,
                  "rank": "1",
                  "loggedIn": "false",
                  "skillvalues": [
                    {"id": 1, "level": 99, "xp": 1300000000, "rank": 5},
                    {"id": 0, "level": 80, "xp":  500000000, "rank": 50}
                  ],
                  "activities": []
                }
                """;

        when(cacheRepository.findByUsernameIgnoreCase("ZombieW")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(profileWithReversedSkills);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(HISCORES_CSV);

        PlayerProfileResponse result = service.getFullProfile("ZombieW");

        assertThat(result.skills()).hasSize(2);
        assertThat(result.skills().get(0).id()).isEqualTo(0);
        assertThat(result.skills().get(1).id()).isEqualTo(1);
        assertThat(result.skills().get(0).xp()).isEqualTo(50_000_000L);
        assertThat(result.skills().get(1).xp()).isEqualTo(130_000_000L);
    }

    // ── fetchFromApis — null skillValues and activities ───────────────────────

    @Test
    void fetchFromApis_nullSkillValues_returnsEmptySkills() {
        String profileWithNullSkills = """
                {
                  "name": "Zezima",
                  "combatlevel": 138,
                  "totalxp": 5000000,
                  "totalskill": 2595,
                  "questscomplete": 300,
                  "questsstarted": 10,
                  "questsnotstarted": 5,
                  "rank": "1",
                  "loggedIn": "true",
                  "skillvalues": null,
                  "activities": null
                }
                """;

        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(profileWithNullSkills);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(HISCORES_CSV);

        PlayerProfileResponse result = service.getFullProfile("Zezima");

        assertThat(result.skills()).isEmpty();
        assertThat(result.recentActivities()).isEmpty();
        assertThat(result.loggedIn()).isTrue();
    }

    // ── getXpGained ───────────────────────────────────────────────────────────

    @Test
    void getXpGained_withNoSnapshots_returnsEmpty() {
        when(snapshotRepository.findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(
                anyString(), any())).thenReturn(Collections.emptyList());

        assertThat(service.getXpGained("Zezima", 30)).isEmpty();
    }

    @Test
    void getXpGained_withOnlyOneDay_returnsEmpty() {
        // Two snapshots on the same calendar day → 1 distinct date → cannot compute gain
        LocalDateTime now = LocalDateTime.now();
        List<XpSnapshot> snaps = List.of(
                new XpSnapshot("zezima", 0, 10_000_000L, now.minusHours(3)),
                new XpSnapshot("zezima", 0, 10_500_000L, now.minusHours(1))
        );
        when(snapshotRepository.findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(
                anyString(), any())).thenReturn(snaps);

        assertThat(service.getXpGained("Zezima", 30)).isEmpty();
    }

    @Test
    void getXpGained_withTwoDays_returnsGainsSortedDescending() {
        LocalDateTime day1 = LocalDateTime.now().minusDays(2);
        LocalDateTime day2 = LocalDateTime.now().minusDays(1);
        List<XpSnapshot> snaps = List.of(
                new XpSnapshot("zezima", 0, 10_000_000L, day1),   // Attack: start
                new XpSnapshot("zezima", 0, 10_500_000L, day2),   // Attack: +500k
                new XpSnapshot("zezima", 2, 20_000_000L, day1),   // Strength: start
                new XpSnapshot("zezima", 2, 21_000_000L, day2)    // Strength: +1M
        );
        when(snapshotRepository.findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(
                anyString(), any())).thenReturn(snaps);

        List<SkillXpGained> result = service.getXpGained("Zezima", 30);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).skillName()).isEqualTo("Strength");
        assertThat(result.get(0).xpGained()).isEqualTo(1_000_000L);
        assertThat(result.get(1).skillName()).isEqualTo("Attack");
        assertThat(result.get(1).xpGained()).isEqualTo(500_000L);
    }

    @Test
    void getXpGained_withNoGainInPeriod_excludesSkill() {
        LocalDateTime day1 = LocalDateTime.now().minusDays(2);
        LocalDateTime day2 = LocalDateTime.now().minusDays(1);
        List<XpSnapshot> snaps = List.of(
                new XpSnapshot("zezima", 0, 10_000_000L, day1),
                new XpSnapshot("zezima", 0, 10_000_000L, day2) // same XP → 0 gain
        );
        when(snapshotRepository.findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(
                anyString(), any())).thenReturn(snaps);

        assertThat(service.getXpGained("Zezima", 30)).isEmpty();
    }

    @Test
    void getXpGained_clampsExcessiveDays() {
        when(snapshotRepository.findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(
                anyString(), any())).thenReturn(Collections.emptyList());

        assertThat(service.getXpGained("Zezima", 9999)).isEmpty();
    }

    @Test
    void getXpGained_multipleSnapshotsPerDay_usesMaxXpPerDay() {
        LocalDateTime day1am = LocalDateTime.now().minusDays(1).withHour(8);
        LocalDateTime day1pm = LocalDateTime.now().minusDays(1).withHour(20);
        LocalDateTime day2 = LocalDateTime.now();
        List<XpSnapshot> snaps = List.of(
                new XpSnapshot("zezima", 0, 10_000_000L, day1am),
                new XpSnapshot("zezima", 0, 10_800_000L, day1pm), // max for day1
                new XpSnapshot("zezima", 0, 11_000_000L, day2)
        );
        when(snapshotRepository.findByUsernameIgnoreCaseAndRecordedAtAfterOrderBySkillIdAscRecordedAtAsc(
                anyString(), any())).thenReturn(snaps);

        List<SkillXpGained> result = service.getXpGained("Zezima", 30);

        assertThat(result).hasSize(1);
        // gain = 11M - 10.8M = 200k (uses 10.8M as day1 max, not 10.0M)
        assertThat(result.get(0).xpGained()).isEqualTo(200_000L);
    }

    // ── saveCache edge cases ──────────────────────────────────────────────────

    @Test
    void saveCache_withExistingCacheEntry_updatesEntry() throws Exception {
        CharacterCache existing = new CharacterCache();
        existing.setUsername("zezima");
        existing.setLastFetched(LocalDateTime.now().minusMinutes(10));
        existing.setCachedResponse(objectMapper.writeValueAsString(buildProfile("Old")));

        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.of(existing));
        stubApiCalls();

        service.getFullProfile("Zezima");

        verify(cacheRepository).save(existing); // same object, updated
    }

    @Test
    void saveCache_whenSaveThrows_doesNotPropagate() throws Exception {
        when(cacheRepository.findByUsernameIgnoreCase("Zezima")).thenReturn(Optional.empty());
        stubApiCalls();
        when(cacheRepository.save(any())).thenThrow(new RuntimeException("DB write failed"));

        // Should not throw — cache failure is non-fatal
        PlayerProfileResponse result = service.getFullProfile("Zezima");
        assertThat(result).isNotNull();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void stubApiCalls() {
        when(restTemplate.getForObject(contains("runemetrics/profile"), eq(String.class), anyString()))
                .thenReturn(PROFILE_JSON);
        when(restTemplate.getForObject(contains("runemetrics/quests"), eq(String.class), anyString()))
                .thenReturn(QUESTS_JSON);
        when(restTemplate.getForObject(contains("hiscore"), eq(String.class), anyString()))
                .thenReturn(HISCORES_CSV);
    }

    private PlayerProfileResponse buildProfile(String name) {
        return new PlayerProfileResponse(name, 138, 5000000L, 2595, 300, 10, 5, "1", false,
                List.of(), List.of(), List.of(), List.of());
    }

    private CharacterCache freshCache(PlayerProfileResponse profile) throws Exception {
        CharacterCache entry = new CharacterCache();
        entry.setUsername(profile.username().toLowerCase());
        entry.setLastFetched(LocalDateTime.now());
        entry.setCachedResponse(objectMapper.writeValueAsString(profile));
        return entry;
    }

    /** 30 skill rows + one boss row with rank=5, killCount=250 */
    private static String buildHiscoresCsv() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append("1,99,14000000\n");
        }
        // First boss row: Bounty Hunter, rank=5, killCount=250
        sb.append("5,250,250\n");
        return sb.toString();
    }

    private static String buildHiscoresCsvWithZeroKills() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append("1,99,14000000\n");
        }
        // Boss row with killCount = 0
        sb.append("1,0,0\n");
        return sb.toString();
    }

    private static String buildHiscoresCsvWithShortBossLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append("1,99,14000000\n");
        }
        // Only 1 field — should be skipped
        sb.append("5\n");
        return sb.toString();
    }

    private static String buildHiscoresCsvWithNonNumericBossLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append("1,99,14000000\n");
        }
        // Non-numeric fields — NumberFormatException should be swallowed
        sb.append("abc,def,ghi\n");
        return sb.toString();
    }
}
