package io.clue2solve.spring.cloud.starter.youtube.autoconfig;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class YouTubeClientFactoryAutoConfig {

	@Value("${youtube.serviceaccount.file}")
	private String serviceAccountFile;

	@Bean
	public YouTube youTubeClientFactory() throws GeneralSecurityException, IOException {
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

		GoogleCredentials credentials = ServiceAccountCredentials
			.fromStream(new ClassPathResource(serviceAccountFile).getInputStream())
			.createScoped(Collections.singleton(YouTubeScopes.YOUTUBE_FORCE_SSL));

		return new YouTube.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
			.setApplicationName("your-application-name")
			.build();
	}

}