package io.clue2solve.spring.cloud.starter.youtube.autoconfig;

import io.clue2solve.spring.cloud.starter.youtube.config.GoogleOAuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@ConditionalOnClass(GoogleOAuthProperties.class)
@ConditionalOnProperty(name = { "spring.security.oauth2.client.registration.google.client-id",
		"spring.security.oauth2.client.registration.google.client-secret",
		"spring.security.oauth2.client.registration.google.redirect-uri",
		"spring.security.oauth2.client.registration.google.scope" })
public class GoogleAuthAutoConfiguration {

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.registration.google.scope}")
	private String scope;

	@Bean
	public GoogleOAuthProperties googleOAuthProperties() {
		return new GoogleOAuthProperties(clientId, clientSecret, redirectUri, scope);
	}

}
