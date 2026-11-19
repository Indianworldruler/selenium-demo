package com.itm.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public final class ExtentManager {
    private static ExtentReports extent;

    private ExtentManager() { }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            Path reports = Paths.get(System.getProperty("user.dir"), "reports");
            try {
                Files.createDirectories(reports);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create reports directory", e);
            }
            ExtentSparkReporter spark = new ExtentSparkReporter(
                    reports.resolve("ExtentReport.html").toString());
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "SauceDemo");
            extent.setSystemInfo("Framework", "Selenium + TestNG + POM");
        }
        return extent;
    }
}
