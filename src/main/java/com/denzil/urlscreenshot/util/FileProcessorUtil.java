package com.denzil.urlscreenshot.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denzil Gideon M. Daulo
 * Utility Class that is responsible for reading the text file and processing them into a list
 */
public class FileProcessorUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(FileProcessorUtil.class);

    private FileProcessorUtil() {
    }

    public static List<String> processFile(String filepath) throws IOException {
        List<String> urlWebsiteList = new ArrayList<>();
        LOGGER.info("Processing file:{}", filepath);

        try {
            File f = new File(filepath);

            LOGGER.info("Reading files contents:");

            List<String> urlLines = FileUtils.readLines(f, "UTF-8");

            for (String url : urlLines) {
                System.out.println(url);
                urlWebsiteList.add(url);
            }

        } catch (IOException e) {
            LOGGER.error("Unable to read files due to:{}", e.getMessage());
            throw new IOException("Unable to read files due to"+ e.getMessage());
        }
        return urlWebsiteList;
    }
}
