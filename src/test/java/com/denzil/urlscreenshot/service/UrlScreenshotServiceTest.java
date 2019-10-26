package com.denzil.urlscreenshot.service;

import com.denzil.urlscreenshot.dto.UrlScreenShotDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Import(TestContextConfiguration.class)
@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
public class UrlScreenshotServiceTest {

    @Autowired
    private UrlScreenshotService urlScreenshotService;

    @Test
    public void shouldScreenshotWebsiteSuccessfully() throws IOException {
        String url = "https://www.google.com";

        UrlScreenShotDTO urlScreenShotDTO = urlScreenshotService.screenshotWebsite(url);
        assertNotNull(urlScreenShotDTO);
        assertNotNull(urlScreenShotDTO.getScreenShotFilePath());
        assertThat(urlScreenShotDTO.getUrlName(), is("google"));
    }

}
