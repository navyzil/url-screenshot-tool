package com.denzil.urlscreenshot;

import com.denzil.urlscreenshot.component.CommandParserComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

/**
 * UrlScreenshotApp Version 1
 * @author  Denzil Gideon M. Daulo
 *
 * Command line arguments:
 * list - Displays all of the url screenshot stored in AWS S3
 *  command syntax: list <bucket-name> for displaying all of screenshots data stored in AWS S3 under the Bucket Name
 *                  list <bucket-name> <keyword> displays screenshots data stored in AWS S3 under Bucket Name that has a keyword
 * get - Downloads the url screenshot stored in AWS S3
 *  command syntax: get <bucket-name> <keyname> <download-path> downloads the screenshot <keyname> that is stored in AWS S3 under the Bucket Name to the <download-path>
 *
 * capture - Screenshots the URL and Stores the backup screenshot to AWAS S3
 *  command syntax: capture <bucket-name> in <url-1>,<url-2>,...<url-n> screenshots and stores to AWS S3 under Bucket Name for comma separated inline urls
 *  command syntax: capture <bucket-name> file <list-of-url-text-file-filepath> screenshots and stores to AWS S3 under Bucket Name for urls that are stored in a text file.
 *  A full path of the file must be given for using this command
 */
@SpringBootApplication
public class UrlScreenshotApp implements CommandLineRunner
{
    @Autowired
    private CommandParserComponent commandParserComponent;

    public static void main( String[] args )
    {
        System.out.println("Welcome to UrlScreenshotApp V.01");

        SpringApplication app = new SpringApplication(UrlScreenshotApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Preparing to execute command:" + args);
        commandParserComponent.parseCommand(args);
        System.out.println("Processed finished... Exiting App");
        exit(0);
    }
}
