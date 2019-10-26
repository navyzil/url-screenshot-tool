package com.denzil.urlscreenshot.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class FileProcessorUtilTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void shouldReadAndExecuteFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:url-files-100entries.txt");
        resource.getInputStream();
        File file = resource.getFile();

        String fileName = file.getAbsolutePath();

        List<String> stringList = FileProcessorUtil.processFile(fileName);

        assertFalse(stringList.isEmpty());
    }

}
