package com.fullstackapp.rs3tracker.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;

class EntityGetterTest {

    @Test
    void characterCache_gettersAndSetters() {
        CharacterCache cache = new CharacterCache();
        assertThat(cache.getId()).isNull();
        cache.setUsername("zezima");
        assertThat(cache.getUsername()).isEqualTo("zezima");
        LocalDateTime now = LocalDateTime.now();
        cache.setLastFetched(now);
        assertThat(cache.getLastFetched()).isEqualTo(now);
        cache.setCachedResponse("{\"username\":\"zezima\"}");
        assertThat(cache.getCachedResponse()).isEqualTo("{\"username\":\"zezima\"}");
    }

}
