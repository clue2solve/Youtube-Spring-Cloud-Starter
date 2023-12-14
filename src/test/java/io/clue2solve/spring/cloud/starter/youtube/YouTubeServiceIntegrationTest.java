package io.clue2solve.spring.cloud.starter.youtube;

import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

import ch.qos.logback.classic.Logger;
import io.clue2solve.spring.cloud.starter.youtube.model.CaptionInfo;
import io.clue2solve.spring.cloud.starter.youtube.model.VideoDetails;
import io.clue2solve.spring.cloud.starter.youtube.services.CaptionService;
import io.clue2solve.spring.cloud.starter.youtube.services.YouTubeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TestInit.class)
@ActiveProfiles("authorized")
public class YouTubeServiceIntegrationTest {

	Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(YouTubeServiceIntegrationTest.class);

	@Autowired
	private YouTubeService youTubeService;

	@Autowired
	private CaptionService captionService;

	@Test
	public void getVideoTest() throws Exception {
		Video video = youTubeService.getVideo("9SGDpanrc8U"); // replace with a valid
																// video ID
		// Get the video snippet
		VideoSnippet snippet = video.getSnippet();
		if (snippet != null) {
			String title = snippet.getTitle();
			String description = snippet.getDescription();
			ThumbnailDetails thumbnails = snippet.getThumbnails();
			logger.info("Title: {}", title);
			logger.info("Description: {}", description);
			// You can get default, medium, high, standard, maxres thumbnails from
			// ThumbnailDetails
		}
		assertNotNull(video);

	}

	@Test
	public void getCaptionInfoTest() throws Exception {
		List<CaptionInfo> captionInfoList = youTubeService.getCaptionInfo("9SGDpanrc8U");
		// Assert that the caption info list is not null
		assertNotNull(captionInfoList);

		// Optionally, assert that the list is not empty
		assertFalse(captionInfoList.isEmpty());

		// Optionally, log some information about the captions
		for (CaptionInfo captionInfo : captionInfoList) {
			logger.info("Caption ID: {}", captionInfo.id());
			logger.info("Language: {}", captionInfo.language());
			logger.info("Track Kind: {}", captionInfo.trackKind());
			logger.info("Is Draft: {}", captionInfo.isDraft());
			logger.info("Is Auto Synced: {}", captionInfo.isAutoSynced());
		}
	}

	@Test
	public void getVideoDetailsTest() throws Exception {
		VideoDetails videoDetails = youTubeService.getVideoDetails("9SGDpanrc8U");

		// Assert that the video details object is not null
		assertNotNull(videoDetails);

		// Optionally, log some information about the video details
		logger.info("Video ID: {}", videoDetails.id());
		logger.info("Title: {}", videoDetails.title());
		logger.info("Description: {}", videoDetails.description());
		logger.info("View Count: {}", videoDetails.viewCount());
		logger.info("Like Count: {}", videoDetails.likeCount());
		logger.info("Duration: {}", videoDetails.duration());
		logger.info("Number of Captions: {}", videoDetails.numberOfCaptions());
		logger.info("Caption Languages: {}", videoDetails.captionLanguages());
	}

	// NEed to figure out how to test this with a real Token
	public void downloadCaptionTest() throws Exception {
	}

}