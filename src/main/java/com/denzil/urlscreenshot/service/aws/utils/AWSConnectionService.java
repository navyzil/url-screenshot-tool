package com.denzil.urlscreenshot.service.aws.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSConnectionService {
    private static Logger LOGGER = LoggerFactory.getLogger(AWSConnectionService.class);

    private String awsAccessKey;
    private String secretAccessKey;

    private AWSStaticCredentialsProvider awsStaticCredentialsProvider;

    public AWSConnectionService(String awsAccessKey, String secretAccessKey) {
        this.awsAccessKey = awsAccessKey;
        this.secretAccessKey = secretAccessKey;
    }

    private AWSStaticCredentialsProvider connectUsingSecurityToken() {
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
        return new AWSStaticCredentialsProvider(sessionCredentials);
    }

    private AWSStaticCredentialsProvider connectUsingBasicCredentials() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsAccessKey, secretAccessKey);
        return new AWSStaticCredentialsProvider(basicAWSCredentials);
    }

    public AWSStaticCredentialsProvider getAwsStaticCredentialsProvider() {
        try {
            if(awsAccessKey.isEmpty() && secretAccessKey.isEmpty())
            {
                LOGGER.warn("AWSAccessKey and SecretAccessKey are required for Basic Credentials Connection");
                throw new AmazonServiceException("AWSAccessKey and SecretAccessKey are required for Basic Credentials Connection");
            }
            awsStaticCredentialsProvider = connectUsingBasicCredentials();
        } catch (AmazonServiceException e) {
            LOGGER.warn("Unable to access aws using credentials:{}-{}", awsAccessKey, secretAccessKey);
            awsStaticCredentialsProvider = connectUsingSecurityToken();
        }
        return awsStaticCredentialsProvider;
    }
}
