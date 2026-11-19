package com.itm.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Demo-only animation; assertions still use explicit waits. */
public final class DemoActions {
    private static final boolean VISIBLE =
            Boolean.parseBoolean(System.getProperty("demo.visible", "true"));
    private static final int TYPING_MS = Integer.getInteger("demo.typingMs", 160);
    private static final int ACTION_MS = Integer.getInteger("demo.actionMs", 900);
    private static final int RESULT_MS = Integer.getInteger("demo.resultMs", 2500);

    private DemoActions() { }

    public static void pause(long millis) {
        if (!VISIBLE || millis <= 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Test demonstration interrupted", e);
        }
    }

    public static void pauseForResult() {
        pause(RESULT_MS);
    }

    public static void highlight(WebDriver driver, WebElement element) {
        if (!VISIBLE) return;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object previous = js.executeScript(
                "var x=arguments[0].style.outline; " +
                "arguments[0].style.outline='3px solid orange'; return x;", element);
        pause(500);
        js.executeScript("arguments[0].style.outline=arguments[1];",
                element, previous);
    }

    public static void type(WebDriver driver, WebElement element, String value) {
        highlight(driver, element);
        element.clear();
        if (VISIBLE) {
            for (char c : value.toCharArray()) {
                element.sendKeys(Character.toString(c));
                pause(TYPING_MS);
            }
        } else {
            element.sendKeys(value);
        }
        pause(ACTION_MS);
    }

    public static void click(WebDriver driver, WebElement element) {
        highlight(driver, element);
        pause(ACTION_MS);
        element.click();
        pause(ACTION_MS);
    }
}
