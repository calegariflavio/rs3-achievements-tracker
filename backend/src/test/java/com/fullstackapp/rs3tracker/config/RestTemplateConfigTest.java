package com.fullstackapp.rs3tracker.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    @Test
    void restTemplate_returnsRestTemplateInstance() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate rt = config.restTemplate();
        assertThat(rt).isNotNull().isInstanceOf(RestTemplate.class);
    }
}
