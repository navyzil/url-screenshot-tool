package com.denzil.urlscreenshot.dto;

import java.io.File;

/**
 * @author Denzil Gideon M. Daulo
 * A DTO that holds the information about the website that will be screenshot and passed around for processing
 */
public class UrlScreenShotDTO {
    private File screenShotFilePath;
    private String urlName;
    private String eTag;

    public File getScreenShotFilePath() {
        return screenShotFilePath;
    }

    public void setScreenShotFilePath(File screenShotFilePath) {
        this.screenShotFilePath = screenShotFilePath;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
