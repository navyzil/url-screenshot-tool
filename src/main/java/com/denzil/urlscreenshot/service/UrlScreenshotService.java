package com.denzil.urlscreenshot.service;

import com.denzil.urlscreenshot.dto.UrlScreenShotDTO;

import java.io.IOException;

public interface UrlScreenshotService {
    UrlScreenShotDTO screenshotWebsite(String url) throws IOException;
}
