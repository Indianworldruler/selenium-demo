package com.itm.listeners;

import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentTest;

import com.itm.utils.DemoActions;
import com.itm.utils.DriverFactory;
import com.itm.utils.ExtentManager;
import com.itm.utils.ScreenshotUtil;

public class TestListener implements ITestListener {
    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest reportTest = ExtentManager.getInstance().createTest(
                result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName());
        CURRENT.set(reportTest);
        LOGGER.info("START: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (CURRENT.get() != null) CURRENT.get().pass("Test passed");
        LOGGER.info("PASS: {}", result.getName());
        CURRENT.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("FAIL: {}", result.getName(), result.getThrowable());
        ExtentTest reportTest = CURRENT.get();
        if (reportTest != null) reportTest.fail(result.getThrowable());
        WebDriver driver = DriverFactory.currentDriver();
        if (driver != null) {
            try {
                Path screenshot = ScreenshotUtil.captureScreenshot(driver,
                        result.getTestClass().getRealClass().getSimpleName()
                        + "_" + result.getName());
                if (reportTest != null) {
                    reportTest.addScreenCaptureFromPath(
                            "../screenshots/" + screenshot.getFileName().toString());
                }
                LOGGER.info("Failure screenshot: {}", screenshot);
            } catch (Exception ex) {
                LOGGER.warn("Could not capture failure screenshot", ex);
            }
        }
        // Keep Chrome open briefly after a failure so the screen can be inspected.
        DemoActions.pause(Long.getLong("demo.failurePauseMs", 12000L));
        CURRENT.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (CURRENT.get() != null) CURRENT.get().skip("Test skipped");
        LOGGER.warn("SKIPPED: {}", result.getName());
        CURRENT.remove();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
        LOGGER.info("SUITE FINISHED: {}", context.getName());
    }
}
