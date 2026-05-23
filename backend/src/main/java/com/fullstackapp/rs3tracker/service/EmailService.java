package com.fullstackapp.rs3tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.url}")
    private String appUrl;

    public void sendVerificationEmail(String to, String token) {
        String verifyUrl = appUrl + "/verify?token=" + token;

        if (apiKey.isBlank()) {
            log.warn("RESEND_API_KEY not configured — skipping email to {}", to);
            log.warn("Manual verification URL: {}", verifyUrl);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                    "from",    fromAddress,
                    "to",      List.of(to),
                    "subject", "Verify your RS3 Tracker account",
                    "html",    buildEmailBody(verifyUrl)
            );

            restTemplate.postForObject(RESEND_URL, new HttpEntity<>(body, headers), String.class);
            log.info("Verification email sent to {}", to);
        } catch (Exception e) {
            log.warn("Could not send verification email to {}: {}", to, e.getMessage());
            log.warn("Manual verification URL: {}", verifyUrl);
        }
    }

    private String buildEmailBody(String verifyUrl) {
        return """
                <div style="font-family:sans-serif;max-width:480px;margin:0 auto;padding:24px;">
                  <h2 style="color:#d97706;margin-bottom:8px;">RS3 Achievement Tracker</h2>
                  <p style="color:#d6d3d1;margin-bottom:20px;">
                    Thanks for signing up! Click the button below to verify your email address and activate your account.
                  </p>
                  <p style="margin:28px 0;">
                    <a href="%s"
                       style="background:#d97706;color:#1c1917;padding:14px 28px;border-radius:8px;
                              text-decoration:none;font-weight:bold;font-size:15px;display:inline-block;">
                      Verify my account
                    </a>
                  </p>
                  <p style="color:#78716c;font-size:13px;">
                    This link expires in 24 hours. If you didn't create an account, you can safely ignore this email.
                  </p>
                </div>
                """.formatted(verifyUrl);
    }
}
