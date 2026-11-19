package com.itm.utils;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Reusable explicit waits; never replace these with fixed sleep calls. */
public final class WaitUtil {
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private WaitUtil() { }

    public static WebElement visible(WebDriver driver, By locator) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement clickable(WebDriver driver, By locator) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean urlContains(WebDriver driver, String value) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.urlContains(value));
    }
}
