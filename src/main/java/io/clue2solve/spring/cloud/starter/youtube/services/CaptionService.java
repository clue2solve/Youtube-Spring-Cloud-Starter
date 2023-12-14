package io.clue2solve.spring.cloud.starter.youtube.services;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Captions;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;

import io.clue2solve.spring.cloud.starter.youtube.config.GoogleOAuthProperties;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CaptionService {

	Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(CaptionService.class);

	private final YouTube youtube;

	private String clientId;

	private String clientSecret;

	public CaptionService(YouTube youtube, GoogleOAuthProperties googleOAuthProperties) {
		this.youtube = youtube;
		this.clientId = googleOAuthProperties.clientId();
		this.clientSecret = googleOAuthProperties.clientSecret();
		logger.info("Caption Service Constructor :: Client ID: " + googleOAuthProperties.clientId());
		logger.info("Caption Service Constructor :: Client Secret: " + googleOAuthProperties.clientSecret());
	}

	public String downloadCaption(String videoId, String language, String authCode) {

		logger.info("Auth Code: " + authCode);
		// logger.info("Access Token: " + getAccessToken(authCode));
		var accessToken = getAccessToken(authCode);
		// Now download the caption
		// Instantiate a Youtube object to download the caption using the accessToken
		YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
				new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request) throws IOException {
						request.getHeaders().setAuthorization("Bearer " + accessToken);
					}
				})
			.setApplicationName("Caption")
			.build();

		// Get the caption
		CaptionListResponse captionResponse = null;
		try {
			captionResponse = youtube.captions().list(Collections.singletonList("snippet"), videoId).execute();
		}
		catch (IOException e) {
			logger.error("Error getting caption list", e.getMessage());
			return null; // Return null if there's an error getting the caption list
		}
		List<Caption> captions = captionResponse.getItems();
		logger.info("Captions: " + captions.toString());

		for (Caption caption : captions) {
			logger.info("Caption: " + caption.toString());
  			String captionLanguage = caption.getSnippet().getLanguage();
			boolean isTrackDownloadable = caption.getSnippet().isTr;

			YouTube.Captions.Download captionDownload = null;
			try {
				captionDownload = youtube.captions().download(caption.getId());
				logger.info("Caption Download: " + captionDownload.toString());

				captionDownload.getMediaHttpDownloader().setDirectDownloadEnabled(true);
				InputStream captionStream = captionDownload.executeMediaAsInputStream();

			}
			catch (IOException e) {
				logger.error("Error downloading caption", e);
				return null;
			}
		}

		return null;
	}

	public String getAccessToken(String authCode) {
		logger.info("Auth Code inside Caption Service: " + authCode);
		// Set up the NetHttpTransport and GsonFactory
		NetHttpTransport transport = new NetHttpTransport();
		GsonFactory jsonFactory = new GsonFactory();

		logger.info("Client ID: " + clientId.toString());
		logger.info("Client Secret: " + clientSecret.toString());

		// Load client secrets
		GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
		web.setClientId(clientId);
		web.setClientSecret(clientSecret);
		GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);

		// Build flow and trigger user authorization request
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory,
				clientSecrets, Arrays.asList("https://www.googleapis.com/auth/youtube",
						"https://www.googleapis.com/auth/youtube.force-ssl"))
			.build();

		// Exchange the authorization code for an access token
		GoogleTokenResponse response = null;
		try {
			response = flow.newTokenRequest(authCode).setRedirectUri("http://localhost:3000").execute();
		}
		catch (TokenResponseException e) {
			logger.error("Error getting access token: " + e.getDetails() + " " + e.getMessage());
			return null; // Return null if there's an error getting the access token
		}
		catch (IOException e) {
			logger.error("Error exchanging authorization code for access token", e);
			return null; // Return null if there's an error exchanging the code for an
							// access token
		}

		logger.info("Access Token Response: " + response.toString());

		// Return the access token
		return response.getAccessToken();

	}

}