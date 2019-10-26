package com.denzil.urlscreenshot.service.s3.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Denzil Gideon M. Daulo
 * Service that is responsible for connecting to AWS and creating the S3 Client to be used
 */
@Service
public class S3ConnectionService {
    private static Logger LOGGER = LoggerFactory.getLogger(S3ConnectionService.class);

    private AWSCredentials credentials;
    private AWSStaticCredentialsProvider awsStaticCredentialsProvider;
    private AmazonS3 amazonS3;

    public S3ConnectionService(String awsAccessKey, String secretAccessKey)
    {
        credentials = new BasicAWSCredentials(awsAccessKey, secretAccessKey);
    }

    public AmazonS3 connectToS3()
    {
        try {
            return connectUsingBasicCredentials();
        }
        catch(AmazonS3Exception e)
        {
            return connectUsingSecurityToken();
        }
    }

    private AmazonS3 connectUsingSecurityToken() {
        LOGGER.warn("Application will connect using Security Token Service");
        AWSSecurityTokenService sts_client = AWSSecurityTokenServiceClientBuilder.standard().build();
        GetSessionTokenRequest sessionTokenRequest = new GetSessionTokenRequest();
        GetSessionTokenResult sessionToken =
                sts_client.getSessionToken(sessionTokenRequest);
        Credentials credentials = sessionToken.getCredentials();
        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                credentials.getAccessKeyId(),
                credentials.getSecretAccessKey(),
                credentials.getSessionToken());
        awsStaticCredentialsProvider =  new AWSStaticCredentialsProvider(sessionCredentials);
        amazonS3 = generateAmazonS3Builder(awsStaticCredentialsProvider);
        return amazonS3;
    }

    private AmazonS3 connectUsingBasicCredentials() {
        awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(credentials);
        amazonS3 = generateAmazonS3Builder(awsStaticCredentialsProvider);
        LOGGER.info("checking Connection");
        if(amazonS3.listBuckets().isEmpty())
        {
            LOGGER.warn("Unable to access aws using credentials:{}-{}", credentials.getAWSAccessKeyId(),credentials.getAWSSecretKey());
            throw new AmazonS3Exception("Unable to access aws");
        }
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
