package com.denzil.urlscreenshot.service.aws.s3.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.denzil.urlscreenshot.service.aws.utils.AWSConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Denzil Gideon M. Daulo
 * Service that is responsible for connecting to AWS and creating the S3 Client to be used
 */
@Service
public class S3ConnectionService {
    private static Logger LOGGER = LoggerFactory.getLogger(S3ConnectionService.class);

    @Autowired
    private AWSConnectionService awsConnectionService;

    public AmazonS3 connectToS3()
    {
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = awsConnectionService.getAwsStaticCredentialsProvider();
        AmazonS3 amazonS3 = generateAmazonS3Builder(awsStaticCredentialsProvider);
        return amazonS3;
    }

    private static AmazonS3 generateAmazonS3Builder(AWSStaticCredentialsProvider awsStaticCredentialsProvider) {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(awsStaticCredentialsProvider)
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
    }
}
