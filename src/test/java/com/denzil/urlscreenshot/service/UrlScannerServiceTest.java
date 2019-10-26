package com.denzil.urlscreenshot.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.denzil.urlscreenshot.dto.UrlScreenShotDTO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Import(TestContextConfiguration.class)
@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
public class UrlScannerServiceTest {

    @Autowired
    private UrlScannerService urlScannerService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void shouldScanAndStoreListOfUrl() {
        String url = "https://www.google.com, https://www.yahoo.com, https://www.ikea.com, https://www.amazon.com, https://www.ebay.com";
        String bucketName = "scanner-service-test-bucket";

        List<UrlScreenShotDTO> urlScreenShotDTOList = urlScannerService.scanUrlFromList(bucketName, url);

        assertFalse(urlScreenShotDTOList.isEmpty());
        UrlScreenShotDTO urlScreenShotDTO = urlScreenShotDTOList.get(0);
        assertNotNull(urlScreenShotDTO);
        assertThat(urlScreenShotDTO.getUrlName(), is("google"));
    }

    @Test
    public void shouldScanAndStoreUrlFromFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:url-files.txt");
        resource.getInputStream();
        File file = resource.getFile();

        String fileName = file.getAbsolutePath();
        String bucketName = "scanner-service-test-bucket";

        List<UrlScreenShotDTO> urlScreenShotDTOList = urlScannerService.scanUrlFromFile(bucketName, fileName);

        assertFalse(urlScreenShotDTOList.isEmpty());
        UrlScreenShotDTO urlScreenShotDTO = urlScreenShotDTOList.get(0);
        assertNotNull(urlScreenShotDTO);
        assertThat(urlScreenShotDTO.getUrlName(), is("google"));
    }

    @Ignore("Enable this if we will go for a profiling tests")
    @Test
    public void shouldScanAndStoreManyUrlFromFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:url-files-100Entries.txt");
        resource.getInputStream();
        File file = resource.getFile();

        String fileName = file.getAbsolutePath();
        String bucketName = "scanner-service-test-many-url-file-bucket";

        List<UrlScreenShotDTO> urlScreenShotDTOList = urlScannerService.scanUrlFromFile(bucketName, fileName);

        assertFalse(urlScreenShotDTOList.isEmpty());
        UrlScreenShotDTO urlScreenShotDTO = urlScreenShotDTOList.get(0);
        assertNotNull(urlScreenShotDTO);
        assertThat(urlScreenShotDTO.getUrlName(), is("google"));
    }

    @Test
    public void shouldDisplayAllUrlFromBucket() throws IOException {
        String bucketName = "scanner-service-test-bucket";

        List<S3ObjectSummary> s3ObjectSummaries = urlScannerService.showAllUrlScreenshot(bucketName);

        assertFalse(s3ObjectSummaries.isEmpty());
        S3ObjectSummary s3ObjectSummary = s3ObjectSummaries.get(0);
        assertNotNull(s3ObjectSummary);
    }

    @Test
    public void shouldDisplayUrlFromBucketWithKeyname() throws IOException {
        String bucketName = "scanner-service-test-bucket";
        String keyword = "google";

        List<S3ObjectSummary> s3ObjectSummaries = urlScannerService.showUrlScreenshotByKeyword(bucketName, keyword);

        assertFalse(s3ObjectSummaries.isEmpty());
        S3ObjectSummary s3ObjectSummary = s3ObjectSummaries.get(0);

        assertNotNull(s3ObjectSummary);
        assertTrue(s3ObjectSummary.getKey().contains(keyword));
    }

    @Test
    public void shouldSuccessfullyDownloadScreenshot() throws IOException {
        String bucketName = "scanner-service-test-bucket";
        String keyword = "ikea1570283280989";
        String downloadPath = "/url-screenshots/";

        String downloadedFilePath = urlScannerService.downloadScreenshot(bucketName, keyword, downloadPath);

        assertNotNull(downloadedFilePath);
        assertTrue(downloadedFilePath.contains(keyword));
    }

    @Test
    public void shouldFailDownloadScreenshotIfKeywordDontExist() throws IOException {
        String bucketName = "scanner-service-test-bucket";
        String keyword = "keywordDontExist";
        String downloadPath = "/url-screenshots/";

        String downloadedFilePath = urlScannerService.downloadScreenshot(bucketName, keyword, downloadPath);

        assertNull(downloadedFilePath);
    }

    @Test
    public void shouldFailDownloadScreenshotIfBucketDontExist() throws IOException {
        String bucketName = "new-scanner-service-test-bucket";
        String keyword = "ikea1570283280989";
        String downloadPath = "/url-screenshots/";

        String downloadedFilePath = urlScannerService.downloadScreenshot(bucketName, keyword, downloadPath);

        assertNull(downloadedFilePath);
    }
}
