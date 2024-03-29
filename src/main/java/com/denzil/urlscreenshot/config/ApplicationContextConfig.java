package com.denzil.urlscreenshot.config;

import com.denzil.urlscreenshot.component.CommandParserComponent;
import com.denzil.urlscreenshot.service.UrlScannerService;
import com.denzil.urlscreenshot.service.UrlScreenshotService;
import com.denzil.urlscreenshot.service.aws.dynamodb.DynamoDBConnectionService;
import com.denzil.urlscreenshot.service.impl.UrlScannerServiceImpl;
import com.denzil.urlscreenshot.service.impl.UrlScreenshotServiceImpl;
import com.denzil.urlscreenshot.service.aws.s3.S3StorageService;
import com.denzil.urlscreenshot.service.aws.s3.impl.S3StorageServiceImpl;
import com.denzil.urlscreenshot.service.aws.utils.AWSConnectionService;
import com.denzil.urlscreenshot.service.aws.s3.utils.S3ConnectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Denzil Gideon M. Daulo
 * Application Context that takes care of the bean injection of the beans/instances that are involved.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationContextConfig {
    @Value("${aws.s3.access.key}")
    private String awsAccessKey;
    @Value("${aws.s3.secret.key}")
    private String secretAccessKey;
    @Value("${aws.s3.region}")
    private String s3Region;
    @Value("${aws.dynamodb.endpoint}")
    private String dynamoDbEndPoint;
    @Value("${aws.dynamodb.region}")
    private String dynamoDbRegion;

    @Bean
    public AWSConnectionService awsConnectionService() {
        return new AWSConnectionService(awsAccessKey, secretAccessKey);
    }

    @Bean
    public S3ConnectionService s3ConnectionService() {
        return new S3ConnectionService(s3Region);
    }

    @Bean
    public S3StorageService s3StorageService() {
        return new S3StorageServiceImpl();
    }

    @Bean
    public UrlScreenshotService urlScreenshotService() {
        return new UrlScreenshotServiceImpl();
    }

    @Bean
    public UrlScannerService urlScannerService() {
        return new UrlScannerServiceImpl();
    }

    @Bean
    public CommandParserComponent commandParserComponent() {
        return new CommandParserComponent();
    }

    @Bean
    public DynamoDBConnectionService dynamoDBConnectionService(){
        return new DynamoDBConnectionService(dynamoDbEndPoint, dynamoDbRegion);
    }
}