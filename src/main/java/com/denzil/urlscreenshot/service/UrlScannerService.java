package com.denzil.urlscreenshot.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.denzil.urlscreenshot.dto.UrlScreenShotDTO;

import java.io.IOException;
import java.util.List;

public interface UrlScannerService {
    List<UrlScreenShotDTO> scanUrlFromList(String s3BucketName, String commaSeperatedUrl);

    List<UrlScreenShotDTO> scanUrlFromFile(String s3BucketName, String urlListFileName) throws IOException;

    List<S3ObjectSummary> showAllUrlScreenshot(String s3BucketName);

    List<S3ObjectSummary> showUrlScreenshotByKeyword(String s3BucketName, String keyword);

    String downloadScreenshot(String s3BucketName, String keyName, String downloadPath);
}
