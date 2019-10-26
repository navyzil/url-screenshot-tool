package com.denzil.urlscreenshot.component;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.denzil.urlscreenshot.service.UrlScannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denzil Gideon M. Daulo
 * Command Parser that identifies and process the series of commands that will be executed by the application's services
 */
@Component
public class CommandParserComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandParserComponent.class);

    public static final int FIRST_COMMAND_INDEX = 0;
    public static final int BUCKET_NAME_INDEX = 1;
    public static final int KEYWORD_INDEX = 2;
    public static final int KEYNAME_INDEX = 2;
    public static final int CAPTURE_COMMAND_INDEX = 2;
    public static final int CAPTURE_URL_INDEX = 3;
    public static final int FILE_PATH_INDEX = 3;

    @Autowired
    private UrlScannerService urlScannerService;

    public void parseCommand(String[] commands) {
        String firstCommand = commands[FIRST_COMMAND_INDEX];
        switch (firstCommand) {
            case "list":
                LOGGER.info("executing list command");
                executeListCommand(commands);
                break;
            case "get":
                LOGGER.info("executing get command");
                executeGetCommand(commands);
                break;
            case "capture":
                LOGGER.info("executing capture command");
                executeCaptureCommand(commands);
                break;
            default:
                LOGGER.error("Unrecognized command:{}. Please use list, get or capture", firstCommand);
                throw new RuntimeException("Unrecognized command:" + firstCommand + ". Please use list, get or capture");
        }
    }

    private void executeListCommand(String[] commands) {
        try {
            List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>();
            int numberOfCommands = commands.length-1;
            String bucketName = commands[BUCKET_NAME_INDEX];
            if (numberOfCommands == 2) {
                String keyword = commands[KEYWORD_INDEX];
                s3ObjectSummaries = urlScannerService.showUrlScreenshotByKeyword(bucketName, keyword);
            } else {
                s3ObjectSummaries = urlScannerService.showAllUrlScreenshot(bucketName);
            }
            LOGGER.info("Populating screenshot data from AWS S3 with Bucket-Name:{}:", bucketName);

            s3ObjectSummaries.stream().forEach(s3ObjectSummary -> {
                String csvInformation = "Key:" + s3ObjectSummary.getKey() +
                        "-ETag:" + s3ObjectSummary.getETag() +
                        "-LastModified:" + s3ObjectSummary.getLastModified();
                System.out.println(csvInformation);
            });

        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("Invalid list command syntax. Need additional parameters. Please include a bucket-name and a keyword or a bucketName only.");
            LOGGER.error("list command sample: list <bucket-name> or list <bucket-name> <keyword>");
            throw new RuntimeException("Invalid list command syntax. Need additional parameters. Please include a bucketName and a keyword or a bucketName only." +
                    "\nlist command sample: list <bucket-name> or list <bucket-name> <keyword>");
        }
    }

    private void executeGetCommand(String[] commands) {
        try {
            int numberOfCommands = commands.length-1;
            if (numberOfCommands == 3) {
                String bucketName = commands[BUCKET_NAME_INDEX];
                String keyname = commands[KEYNAME_INDEX];
                String downloadPath = commands[FILE_PATH_INDEX];

                LOGGER.info("Downloading screenshot data with key:{} from AWS S3 with Bucket-Name:{}:", keyname, bucketName);
                String downloadedFilePath = urlScannerService.downloadScreenshot(bucketName, keyname, downloadPath);
                LOGGER.info("Download successful! Please check at location path:", downloadedFilePath);
                System.out.println("Download successful! Please check at location path:" + downloadedFilePath);
            } else {
                LOGGER.error("Invalid get command syntax. Need additional parameters. Please make sure to include the exact key-name and download path.");
                LOGGER.error("get command sample: get <bucket-name> <key-name> <download-path>");
                throw new RuntimeException("Invalid get command syntax. Need additional parameters. Please include both bucket-name, the exact key-name, and the exact download path." +
                        "\nget command sample: get <bucket-name> <key-name> <download-path>");
            }

        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("Invalid get command syntax. Need additional parameters. Please include a bucket-name, the exact key-name, and the exact download path");
            LOGGER.error("get command sample: get <bucket-name> <key-name> <download-path>");
            throw new RuntimeException("Invalid get command syntax. Need additional parameters. Please include a bucket-name, the exact key-name, and the exact download path." +
                    "\nget command sample: get <bucket-name> <key-name> <download-path>");
        }
    }

    private void executeCaptureCommand(String[] commands) {
        try {
            int numberOfCommands = commands.length-1;
            if (numberOfCommands == 3) {
                String bucketName = commands[BUCKET_NAME_INDEX];
                String captureCommand = commands[CAPTURE_COMMAND_INDEX];

                performScreenshotAndStorage(commands, bucketName, captureCommand);
                LOGGER.info("Capture successful! Url Screenshot is backed up in AWS S3 Bucket:{}", bucketName);
                System.out.println("Capture successful! Url Screenshot is backed up in AWS S3 Bucket: " + bucketName);
            } else {
                LOGGER.error("Invalid capture command syntax. Need additional parameters. Please make sure to include the <in> or <file> command and <comma-seperated-url> or <file-path>");
                LOGGER.error("capture command sample for inline : capture in <bucket-name> <url-1>,<url-2>,...<url-n>");
                LOGGER.error("capture command sample for file : capture file <file-path-of-url.txt>");
                throw new RuntimeException("Invalid capture command syntax. Need additional parameters. Please make sure to include the <in> or <file> command and <comma-seperated-url> or <file-path>." +
                        "\ncapture command sample for inline : capture <bucket-name> in <url-1>,<url-2>,...<url-n>" +
                        "\ncapture command sample for file : capture <bucket-name> file <file-path-of-url.txt");
            }

        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("Invalid capture command syntax. Need additional parameters. Please make sure to include the bucket-name, <in> or <file> command and <comma-seperated-url> or <file-path>");
            LOGGER.error("capture command sample for inline : capture in <url-1>,<url-2>,...<url-n>");
            LOGGER.error("capture command sample for file : capture file <file-path-of-url.txt>");
            throw new RuntimeException("Invalid capture command syntax. Need additional parameters. Please make sure to include the <bucket-name>, <in> or <file> command and <comma-seperated-url> or <file-path>." +
                    "\ncapture command sample for inline : capture <bucket-name> in <url-1>,<url-2>,...<url-n>" +
                    "\ncapture command sample for file : capture <bucket-name> file <file-path-of-url.txt");
        }
    }

    private void performScreenshotAndStorage(String[] commands, String bucketName, String captureCommand) {
        switch (captureCommand) {
            case "in":
                String commaSeperatedUrl = commands[CAPTURE_URL_INDEX];
                LOGGER.info("Executing inline url scan");
                urlScannerService.scanUrlFromList(bucketName, commaSeperatedUrl);
                break;
            case "file":
                try {
                    String filePath = commands[FILE_PATH_INDEX];
                    LOGGER.info("Executing url scan from file path:{}", filePath);
                    urlScannerService.scanUrlFromFile(bucketName, filePath);
                } catch (IOException e) {
                    LOGGER.error("Unable to screenshot urls from file:", e.getMessage());
                    throw new RuntimeException("Unable to screenshot urls from file:" + e.getMessage());
                }
                break;
            default:
                LOGGER.error("Unrecognized capture command:{}. Please use in or file", captureCommand);
                throw new RuntimeException("Unrecognized capture command:" + captureCommand + ". Please use in or file");

        }
    }
}
