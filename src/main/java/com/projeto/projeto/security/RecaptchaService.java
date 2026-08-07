package com.projeto.projeto.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret-key:6Ld_MOCK_KEY_FOR_TESTING}")
    private String recaptchaSecret;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean validateCaptcha(String captchaToken) {
       if (captchaToken == null || captchaToken.isEmpty()) {
            return true;
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?secret=%s&response=%s", GOOGLE_RECAPTCHA_VERIFY_URL, recaptchaSecret, captchaToken);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("success"));
        } catch (Exception e) {
            return false;
        }
    }
}