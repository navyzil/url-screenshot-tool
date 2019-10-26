package com.denzil.urlscreenshot.service.aws.s3.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.denzil.urlscreenshot.service.aws.s3.S3StorageService;
import com.denzil.urlscreenshot.service.aws.s3.utils.S3ConnectionService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Denzil Gideon M. Daulo
 * Service that calls on AWS S3 to perform data storage task of the screenshot information to the AWS S3
 * The tasks involves saving and retrieving of the image to S3 and populating information regarding the stored images in S3.
 * It will create a bucket if the bucket is not existing in S3
 */
@Service
public class S3StorageServiceImpl implements S3StorageService {

    private static Logger LOGGER = LoggerFactory.getLogger(S3StorageServiceImpl.class);

    @Autowired
    private S3ConnectionService s3ConnectionService;

    @Override
    public String storeImageToS3(String bucketName, String key, File file) {
        AmazonS3 amazonS3 = s3ConnectionService.connectToS3();
        checkAndCreateBucket(bucketName, amazonS3);
        LOGGER.info("Image will be saved under the bucket name:{}", bucketName);

        String timeStamp = "_"+new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.sss").format(new Date());
        key = key + timeStamp;

        PutObjectResult putObjectResult = amazonS3.putObject(bucketName, key, file);
        return putObjectResult.getETag();
    }

    @Override
    public List<S3ObjectSummary> searchAllImages(String bucketName) {
        LOGGER.info("Retrieving All Images information stored from Bucket:{}", bucketName);
        AmazonS3 amazonS3 = s3ConnectionService.connectToS3();
        checkAndCreateBucket(bucketName, amazonS3);
        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName);

        if (listObjectsV2Result.getObjectSummaries().isEmpty()) {
            LOGGER.info("No images stored to Bucket:{}", bucketName);
        }
        return listObjectsV2Result.getObjectSummaries();
    }

    @Override
    public List<S3ObjectSummary> searchImagesByKey(String bucketName, String key) {
        LOGGER.info("Retrieving All Images information stored from Bucket:{} with key:{}", bucketName, key);
        AmazonS3 amazonS3 = s3ConnectionService.connectToS3();
        checkAndCreateBucket(bucketName, amazonS3);

        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName);

        if (listObjectsV2Result.getObjectSummaries().isEmpty()) {
            LOGGER.info("No images stored to Bucket:{}", bucketName);
        }

        List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>();
        for (S3ObjectSummary s3ObjectSummary : listObjectsV2Result.getObjectSummaries()) {
            if (s3ObjectSummary.getKey().toLowerCase().contains(key.toLowerCase())) {
                s3ObjectSummaries.add(s3ObjectSummary);
            }
        }

        return s3ObjectSummaries;
    }

    @Override
    public String downloadImage(String bucketName, String key, String downloadPath) {
        LOGGER.info("Downloading image with key:{} from Bucket:{}", key, bucketName);
        AmazonS3 amazonS3 = s3ConnectionService.connectToS3();
        checkAndCreateBucket(bucketName, amazonS3);

        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            LOGGER.info("Downloading file from S3 Bucket...");
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            String fileName = downloadPath + key + ".jpg";
            File downloadedFile = new File(fileName);
            FileUtils.copyInputStreamToFile(inputStream, downloadedFile);
            return downloadedFile.getAbsolutePath();

        } catch (AmazonS3Exception e) {
            LOGGER.error("Unable download image with key:{} from Bucket:{}. Please make sure the image exists.", key, bucketName);
            return null;

        } catch (IOException e) {
            LOGGER.error("Unable to download file due to:{}", e.getMessage());
            return null;
        }
    }

    private void checkAndCreateBucket(String bucketName, AmazonS3 amazonS3) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            LOGGER.info("Creating new Bucket to amazon S3 with name:{}", bucketName);
            Bucket bucket = amazonS3.createBucket(bucketName);
            LOGGER.info("Bucket created with following details:{}", bucket.toString());
        }
    }
}
