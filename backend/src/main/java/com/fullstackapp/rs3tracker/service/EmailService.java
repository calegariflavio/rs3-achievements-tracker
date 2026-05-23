package com.fullstackapp.rs3tracker.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.url}")
    private String appUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String verifyUrl = appUrl + "/verify?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("Verify your RS3 Tracker account");
            helper.setText(buildEmailBody(verifyUrl), true);
            mailSender.send(message);
            log.info("Verification email sent to {}", to);
        } catch (Exception e) {
            // In dev/test without SMTP configured, log the URL so you can verify manually
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
