package com.denzil.urlscreenshot.config;

import com.denzil.urlscreenshot.component.CommandParserComponent;
import com.denzil.urlscreenshot.service.UrlScannerService;
import com.denzil.urlscreenshot.service.UrlScreenshotService;
import com.denzil.urlscreenshot.service.impl.UrlScannerServiceImpl;
import com.denzil.urlscreenshot.service.impl.UrlScreenshotServiceImpl;
import com.denzil.urlscreenshot.service.s3.S3StorageService;
import com.denzil.urlscreenshot.service.s3.impl.S3StorageServiceImpl;
import com.denzil.urlscreenshot.service.s3.utils.S3ConnectionService;
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
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Bean
    public S3ConnectionService s3ConnectionService() {
        return new S3ConnectionService(awsS3AccessKey, awsS3SecretKey);
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
}