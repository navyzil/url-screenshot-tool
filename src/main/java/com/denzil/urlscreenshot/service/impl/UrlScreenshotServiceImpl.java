package com.denzil.urlscreenshot.service.impl;

import com.denzil.urlscreenshot.dto.UrlScreenShotDTO;
import com.denzil.urlscreenshot.service.UrlScreenshotService;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Denzil Gideon M. Daulo
 * Service that takes care of the website screenshot
 */
@Service
public class UrlScreenshotServiceImpl implements UrlScreenshotService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${url.screenshot.timeout.secs}")
    public int timeOutInSeconds;

    private static Logger LOGGER = LoggerFactory.getLogger(UrlScreenshotServiceImpl.class);

    public static final int WEBSITE_DOMAIN_NAME_INDEX = 1;

    @Override
    public UrlScreenShotDTO screenshotWebsite(String url) throws IOException {
        String urlName = url.split("\\.")[WEBSITE_DOMAIN_NAME_INDEX];
        String timeStamp = "_"+new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.sss").format(new Date());
        String fileName = urlName + timeStamp + ".jpg";
        final File websiteScreenshotFilePath = new File("screenshot-cache/" + fileName).getAbsoluteFile();
        final WebDriver driver = getWebDriver();
        try {
            LOGGER.info("Opening web page for url: {}", url);
            driver.get(url);
            takeScreenshot(websiteScreenshotFilePath, driver);
        } finally {
            LOGGER.debug("Closing the driver");
            driver.quit();
        }

        UrlScreenShotDTO urlScreenShotDTO = new UrlScreenShotDTO();
        urlScreenShotDTO.setUrlName(fileName);
        urlScreenShotDTO.setScreenShotFilePath(websiteScreenshotFilePath);

        return urlScreenShotDTO;
    }

    private void takeScreenshot(File websiteScreenshot, WebDriver driver) {
        try {
            LOGGER.debug("Rendering and Capturing web page");
            TimeUnit.SECONDS.sleep(timeOutInSeconds);

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            LOGGER.info("Saving screenshot");
            final File outputFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(outputFile, websiteScreenshot);
            LOGGER.info("Screenshot saved: {}", websiteScreenshot);
        } catch (InterruptedException e) {
            LOGGER.error("Unable to Render and Capture due to application timeout:", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Unable to save screenshot due to:",e.getMessage());
        }
    }

    private WebDriver getWebDriver() throws IOException {
        try {
            LOGGER.info("Loading geckodriver.exe");

            Resource resource = resourceLoader.getResource("classpath:support-files/geckodriver.exe");
            File file = resource.getFile();
            LOGGER.info("Loading geckodriver.exe path:{}", file.getAbsolutePath());
            System.setProperty("webdriver.gecko.driver", file.getAbsolutePath());

        } catch (IOException e) {
            try {
                LOGGER.warn("Unable to load geckodriver.exe from classpath:", e.getMessage());
                LOGGER.info("Loading geckodriver.exe from same path as jar");
                System.setProperty("webdriver.gecko.driver", "./geckodriver.exe");
            } catch (RuntimeException e1)
            {
                throw new IOException("Unable to load geckodriver.exe:"+ e1.getMessage());
            }
        }

        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        options.setCapability("marionette", true);
        LOGGER.debug("Creating Firefox Driver");
        return new FirefoxDriver(options);
    }
}
