package io.clue2solve.spring.cloud.starter.youtube.config;

import org.springframework.validation.annotation.Validated;
import org.springframework.boot.context.properties.ConfigurationProperties;

public record GoogleOAuthProperties(String clientId, String clientSecret, String redirectUri, String scope) {
}
