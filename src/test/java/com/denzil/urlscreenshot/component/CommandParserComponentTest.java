package com.denzil.urlscreenshot.component;

import com.denzil.urlscreenshot.service.TestContextConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@Import(TestContextConfiguration.class)
@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
public class CommandParserComponentTest {
    @Autowired
    private CommandParserComponent commandParserComponent;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void shouldNotFailIfUseListCommandBucketNameOnly() {
        String command = "list";
        String bucketName = "scanner-service-test-bucket";
        String [] commandArgs = {command, bucketName};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test
    public void shouldNotFailIfUseListCommandBucketNameAndKeyword() {
        String [] commandArgs = {"list", "scanner-service-test-bucket", "ikea"};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseListCommandOnly() {
        String command = "list";
        String [] commandArgs = {command};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test
    public void shouldNotFailIfUseGetCommandBukcetNameKeyNameDownloadPath() {
        String command = "get";
        String bucketName = "scanner-service-test-bucket";
        String keyName = "amazon1570283302883";
        String downloadPath = "/url-screenshots-download/";
        String [] commandArgs = {command, bucketName, keyName, downloadPath};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseGetCommandButNoDownloadPath() {
        String command = "get";
        String bucketName = "scanner-service-test-bucket";
        String keyName = "amazon1570283302883";
        String [] commandArgs = {command, bucketName, keyName};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseGetCommandButBucketNameOnly() {
        String command = "get";
        String bucketName = "scanner-service-test-bucket";
        String [] commandArgs = {command, bucketName};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseGetCommandOnly() {
        String command = "get";
        String [] commandArgs = {command};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test
    public void shouldNotFailIfUseCaptureCommandUseInLine() {
        String command = "capture";
        String bucketName = "scanner-service-test-bucket";
        String captureCommand = "in";
        String commaSeperatedUrls = "https://www.google.com, https://www.yahoo.com, https://www.ikea.com, https://www.amazon.com, https://www.ebay.com";
        String [] commandArgs = {command, bucketName, captureCommand, commaSeperatedUrls};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test
    public void shouldNotFailIfUseCaptureCommandUseFile() throws IOException {
        String command = "capture";
        String bucketName = "scanner-service-test-bucket";
        String captureCommand = "file";

        Resource resource = resourceLoader.getResource("classpath:url-files.txt");
        resource.getInputStream();
        File file = resource.getFile();
        String fileName = file.getAbsolutePath();

        String [] commandArgs = {command, bucketName, captureCommand, fileName};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseCaptureCommandUseInvalidCombination() throws IOException {
        String command = "capture";
        String bucketName = "scanner-service-test-bucket";
        String captureCommand = "in";

        Resource resource = resourceLoader.getResource("classpath:url-files.txt");
        resource.getInputStream();
        File file = resource.getFile();
        String fileName = file.getAbsolutePath();

        String [] commandArgs = {command, bucketName, captureCommand, fileName};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseCaptureCommandUseInlineButNoUrlList() throws IOException {
        String command = "capture";
        String bucketName = "scanner-service-test-bucket";
        String captureCommand = "in";

        String [] commandArgs = {command, bucketName, captureCommand};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseCaptureCommandUseFileButNoFilePath() throws IOException {
        String command = "capture";
        String bucketName = "scanner-service-test-bucket";
        String captureCommand = "file";

        String [] commandArgs = {command, bucketName, captureCommand};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseCaptureCommandUseBucketNameOnly() throws IOException {
        String command = "capture";
        String bucketName = "scanner-service-test-bucket";

        String [] commandArgs = {command, bucketName};
        commandParserComponent.parseCommand(commandArgs);
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailIfUseCaptureCommandUOnly() throws IOException {
        String command = "capture";
        String bucketName = "scanner-service-test-bucket";

        String [] commandArgs = {command, bucketName};
        commandParserComponent.parseCommand(commandArgs);
    }

}
