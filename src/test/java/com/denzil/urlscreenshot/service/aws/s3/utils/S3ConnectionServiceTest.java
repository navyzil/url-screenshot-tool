package com.denzil.urlscreenshot.service.aws.s3.utils;

import com.amazonaws.services.s3.AmazonS3;

import com.denzil.urlscreenshot.service.TestContextConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@Import(TestContextConfiguration.class)
@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
public class S3ConnectionServiceTest {

    @Autowired
    private S3ConnectionService s3ConnectionService;

    @Test
    public void shouldConnectToAwsS3()
    {
        AmazonS3 amazonS3 = s3ConnectionService.connectToS3();
        assertNotNull(amazonS3);
    }
}
