package io.clue2solve.spring.cloud.starter.youtube.autoconfig;

import com.google.api.services.youtube.YouTube;
import io.clue2solve.spring.cloud.starter.youtube.config.GoogleOAuthProperties;
import io.clue2solve.spring.cloud.starter.youtube.services.CaptionService;
import io.clue2solve.spring.cloud.starter.youtube.services.YouTubeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class CaptionServiceAutoConfiguration {

	// Other @Bean methods...
	@Bean
	public CaptionService captionService(YouTube youTube, GoogleOAuthProperties googleOAuthProperties,
			OAuth2AuthorizedClientService authorizedClentService) {
		return new CaptionService(youTube, googleOAuthProperties, authorizedClentService);
	}

}
