package com.itm.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotUtil {
    private ScreenshotUtil() { }

    public static Path captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            throw new IllegalArgumentException("No browser session is available");
        }
        try {
            Path directory = Paths.get(System.getProperty("user.dir"), "screenshots");
            Files.createDirectories(directory);
            String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String stamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            Path output = directory.resolve(safeName + "_" + stamp + ".png");
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), output, StandardCopyOption.REPLACE_EXISTING);
            return output;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not capture screenshot", ex);
        }
    }
}
