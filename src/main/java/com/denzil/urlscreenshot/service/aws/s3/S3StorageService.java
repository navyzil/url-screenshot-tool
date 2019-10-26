package com.denzil.urlscreenshot.service.aws.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.util.List;

public interface S3StorageService {
    String storeImageToS3(String bucketName, String key, File file);

    List<S3ObjectSummary> searchAllImages(String bucketName);

    List<S3ObjectSummary> searchImagesByKey(String bucketName, String key);

    String downloadImage(String bucketName, String key, String downloadPath);
}
