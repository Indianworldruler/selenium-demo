package com.itm.base;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import com.itm.utils.DemoActions;
import com.itm.utils.DriverFactory;
import com.itm.utils.WaitUtil;

/**
 * Shared test setup. A single browser is intentionally reused throughout
 * this sequential suite because the practical requires visible testing.
 */
public abstract class BaseTest {
    protected WebDriver driver;
    private static final String URL = "https://www.saucedemo.com/";

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        driver = DriverFactory.getDriver();
        driver.get(URL);
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear(); window.sessionStorage.clear();");
        driver.manage().deleteAllCookies();
        driver.navigate().to(URL);
        WaitUtil.visible(driver, By.id("user-name"));
        DemoActions.pause(900);
    }

    @AfterMethod(alwaysRun = true)
    public void showResultBeforeNextTest() {
        DemoActions.pauseForResult();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        DemoActions.pause(3000);
        DriverFactory.quitDriver();
    }
}
