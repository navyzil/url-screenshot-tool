package com.denzil.urlscreenshot.service.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.denzil.urlscreenshot.service.TestContextConfiguration;
import org.joda.time.DateTimeUtils;
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

import static org.junit.Assert.*;

@Import(TestContextConfiguration.class)
@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
public class S3StorageServiceTest {

    @Autowired
    private S3StorageService s3StorageService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void shouldStoreSuccessfullyToS3OnBucket() throws IOException {

        Resource resource = resourceLoader.getResource("classpath:test-images/catsandpuppies1.jpg");
        resource.getInputStream();
        File file = resource.getFile();

        String bucketName="new-test-image-bucket";
        String keyName = "catsAndPuppies1";
        String etag = s3StorageService.storeImageToS3(bucketName, keyName, file);
        assertNotNull(etag);
        assertTrue(!etag.isEmpty());
    }

    @Test
    public void shouldDisplayAllObjectsStoredInS3Bucket()
    {
        String bucketName="new-test-image-bucket";
        List<S3ObjectSummary> s3ObjectSummaries = s3StorageService.searchAllImages(bucketName);
        assertFalse(s3ObjectSummaries.isEmpty());
    }

    @Test
    public void shouldBeEmptyIfNoObjectsStoredInS3Bucket()
    {
        String bucketName="test-image-empty-bucket";
        List<S3ObjectSummary> s3ObjectSummaries = s3StorageService.searchAllImages(bucketName);
        assertTrue(s3ObjectSummaries.isEmpty());
    }

    @Test
    public void shouldDisplayAllObjectsStoredInS3BucketBasedOnGivenKeyword()
    {
        String bucketName="new-test-image-bucket";
        String keyword = "puppies";
        List<S3ObjectSummary> s3ObjectSummaries = s3StorageService.searchImagesByKey(bucketName, keyword);
        assertFalse(s3ObjectSummaries.isEmpty());
    }

    @Test
    public void shouldBeEmptyIfNoObjectsStoredInS3BucketBasedOnGivenKeyword()
    {
        String bucketName="new-test-image-bucket";
        String keyword = "dolphin";
        List<S3ObjectSummary> s3ObjectSummaries = s3StorageService.searchImagesByKey(bucketName, keyword);
        assertTrue(s3ObjectSummaries.isEmpty());
    }

    @Test
    public void shouldDownloadImageBasedOnGivenExistingBucketAndKey()
    {
        String bucketName="new-test-image-bucket";
        String s3ObjectKey = "catsAndPuppies11570261308636";
        String downloadPath = "/Users/user/Desktop/";
        String downloadedFilePath = s3StorageService.downloadImage(bucketName, s3ObjectKey, downloadPath);
        assertTrue(downloadedFilePath.contains(s3ObjectKey+".jpg"));

    }

    @Test
    public void shouldDownloadImageIfAndCreateNewFolder()
    {
        String bucketName="new-test-image-bucket";
        String s3ObjectKey = "catsAndPuppies11570261308636";
        String downloadPath = "/Users/user/Desktop/CreatedFolder-"+ DateTimeUtils.currentTimeMillis()+"/";
        String downloadedFilePath = s3StorageService.downloadImage(bucketName, s3ObjectKey, downloadPath);
        assertTrue(downloadedFilePath.contains(s3ObjectKey+".jpg"));
    }

    @Test
    public void shouldFailDownloadImageIfDownloadPathIsInvalid()
    {
        String bucketName="new-test-image-bucket";
        String s3ObjectKey = "catsAndPuppies11570261308636";
        String downloadPath = System.getProperty("user.home")+"Desktop";
        String downloadedFilePath = s3StorageService.downloadImage(bucketName, s3ObjectKey, downloadPath);
        assertNull(downloadedFilePath);

    }

    @Test
    public void shouldFailDownloadImageIfKeyDontExist()
    {
        String bucketName="new-test-image-bucket";
        String s3ObjectKey = "notExistingObject";
        String downloadPath = "/Users/user/Desktop/CreatedFolder-"+ DateTimeUtils.currentTimeMillis()+"/";
        String downloadedFilePath = s3StorageService.downloadImage(bucketName, s3ObjectKey, downloadPath);
        assertNull(downloadedFilePath);
    }
}
