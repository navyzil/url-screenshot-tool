package com.denzil.urlscreenshot.service.impl;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.denzil.urlscreenshot.dto.UrlScreenShotDTO;
import com.denzil.urlscreenshot.service.UrlScannerService;
import com.denzil.urlscreenshot.service.UrlScreenshotService;
import com.denzil.urlscreenshot.service.aws.s3.S3StorageService;
import com.denzil.urlscreenshot.util.FileProcessorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Denzil Gideon M. Daulo
 * Main Service that takes care of organizing and forwarding the website if it will be screenshot and store to S3
 * or retireve the information from the S3
 */
public class UrlScannerServiceImpl implements UrlScannerService {

    private Logger LOGGER = LoggerFactory.getLogger(UrlScreenshotServiceImpl.class);

    @Autowired
    private UrlScreenshotService urlScreenshotService;

    @Autowired
    private S3StorageService s3StorageService;

    @Override
    public List<UrlScreenShotDTO> scanUrlFromList(String s3BucketName, String listOfUrl) {
        LOGGER.info("Scanning websites from inline command");
        List<String> urlList = Arrays.asList(listOfUrl.split(","));
        List<UrlScreenShotDTO> urlScreenShotDTOS = screenShotUrl(urlList);

        storeScreenshots(s3BucketName, urlScreenShotDTOS);

        return urlScreenShotDTOS;
    }

    @Override
    public List<UrlScreenShotDTO> scanUrlFromFile(String s3BucketName, String urlListFileName) throws IOException {
        List<String> listOfUrl = FileProcessorUtil.processFile(urlListFileName);
        List<UrlScreenShotDTO> urlScreenShotDTOS = screenShotUrl(listOfUrl);

        storeScreenshots(s3BucketName, urlScreenShotDTOS);

        return urlScreenShotDTOS;
    }

    private List<UrlScreenShotDTO> screenShotUrl(List<String> listOfUrl) {
        List<UrlScreenShotDTO> urlScreenShotDTOS = new ArrayList<>();

        LOGGER.info("Generating Screenshots");
        for(String urlWebsite: listOfUrl)
        {
            LOGGER.info("Generating Screenshot for website:{}", urlWebsite);
            try {
                UrlScreenShotDTO urlScreenShotDTO = urlScreenshotService.screenshotWebsite(urlWebsite);
                urlScreenShotDTOS.add(urlScreenShotDTO);
            }catch (IOException e)
            {
                LOGGER.error("Unable to generate screenshot:",e.getMessage());
            }
        }

        LOGGER.info("Screenshot successful");
        return urlScreenShotDTOS;
    }

    private void storeScreenshots(String s3BucketName, List<UrlScreenShotDTO> urlScreenShotDTOS) {
        LOGGER.info("Storing screenshot to Storage Bucket:{}", s3BucketName);
        //TODO: create a list then store info to DynamoDB
        urlScreenShotDTOS.stream().forEach(urlScreenShotDTO ->
        {
            String urlName = urlScreenShotDTO.getUrlName();
            File screenShotFilePath = urlScreenShotDTO.getScreenShotFilePath();
            String eTag = s3StorageService.storeImageToS3(s3BucketName, urlName, screenShotFilePath);
            urlScreenShotDTO.setETag(eTag);
        });
    }

    @Override
    public List<S3ObjectSummary> showAllUrlScreenshot(String s3BucketName) {
        LOGGER.info("Displaying all screenshots stored in S3 Bucket:{}", s3BucketName);
        List<S3ObjectSummary> s3ObjectSummaries = s3StorageService.searchAllImages(s3BucketName);
        //TODO: use DynamoDB
        return s3ObjectSummaries;
    }

    @Override
    public List<S3ObjectSummary> showUrlScreenshotByKeyword(String s3BucketName, String keyword) {
        LOGGER.info("Displaying all screenshots stored in S3 Bucket:{} with keyword:{}", s3BucketName, keyword);

        List<S3ObjectSummary> s3ObjectSummaries = s3StorageService.searchImagesByKey(s3BucketName, keyword);
        //TODO: use DynamoDB
        return s3ObjectSummaries;
    }

    @Override
    public String downloadScreenshot(String s3BucketName, String keyName, String downloadPath) {
        LOGGER.info("Downloading screenshot:{} from BucketName:{}", keyName, s3BucketName);
        String downloadMessage = s3StorageService.downloadImage(s3BucketName, keyName, downloadPath);

        if(downloadMessage == null)
        {
            LOGGER.error("Download file with keyname:{} failed! Please check if file exist in Bucket:{}", keyName, s3BucketName);
            return null;
        }
        LOGGER.info("Download file with keyname:{} success! Please check downloaded file at :{}", keyName, downloadPath);

        return downloadMessage;
    }
}
