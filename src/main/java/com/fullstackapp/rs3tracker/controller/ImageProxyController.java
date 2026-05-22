package com.fullstackapp.rs3tracker.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy")
public class ImageProxyController {

    private final RestTemplate restTemplate;

    public ImageProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            requestHeaders.set("Referer", "https://runescape.wiki/");
            requestHeaders.set("Accept", "image/png,image/*,*/*");

            HttpEntity<String> entity = new HttpEntity<>(requestHeaders);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, byte[].class
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.IMAGE_PNG);
            responseHeaders.setCacheControl("public, max-age=86400");

            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
