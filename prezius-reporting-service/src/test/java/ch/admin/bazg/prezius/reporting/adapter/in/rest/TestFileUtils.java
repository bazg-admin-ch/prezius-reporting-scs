package ch.admin.bazg.prezius.reporting.adapter.in.rest;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestFileUtils {

    public static String readResponseBody(String fileName) {
        return readFileString("/response/" + fileName);
    }

    public static String readRequestBody(String fileName) {
        return readFileString("/request/" + fileName);
    }

    private static String readFileString(String fileName) {
        try {
            Resource resource = new ClassPathResource("rest-bodies/".concat(fileName));
            File file = resource.getFile();
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
