package com.itm;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class GoogleSearchTest {

    // Control automation speed
    private final int TYPING_DELAY = 220;
    private final int ACTION_DELAY = 1200;

    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchSeleniumArchitecture() {

        // Keep Chrome open after test execution
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("detach", true);

        WebDriver driver = new ChromeDriver(options);

        WebDriverWait wait = new WebDriverWait(
            driver, Duration.ofSeconds(30)
        );

        // Step 1: Open Google
        driver.manage().window().maximize();
        driver.get("https://www.google.com/");

        System.out.println("Google opened");
        pause(ACTION_DELAY);

        // Step 2: Locate search box
        WebElement searchBox = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.name("q")
            )
        );

        // Step 3: Slowly type search query
        String query = "selenium architecture";

        for (char character : query.toCharArray()) {
            searchBox.sendKeys(String.valueOf(character));
            pause(TYPING_DELAY);
        }

        System.out.println("Search query entered: " + query);
        pause(ACTION_DELAY);

        // Step 4: Press Enter
        searchBox.sendKeys(Keys.ENTER);

        System.out.println("Enter pressed");

        // Step 5: Wait for search results or CAPTCHA
        String outcome = wait.until(d -> {

            if (d.getCurrentUrl().contains("/sorry/")) {
                return "CAPTCHA";
            }

            boolean resultsVisible = d.findElements(
                By.cssSelector("#search h3")
            ).stream().anyMatch(WebElement::isDisplayed);

            return resultsVisible ? "RESULTS" : null;
        });

        if ("CAPTCHA".equals(outcome)) {
            System.out.println(
                "Google requested human verification."
            );
            System.out.println(
                "Chrome will stay open for manual verification."
            );

            throw new SkipException(
                "Search blocked by Google CAPTCHA"
            );
        }

        // Step 6: Verify search results
        Assert.assertEquals(
            outcome,
            "RESULTS",
            "Search results were not displayed"
        );

        System.out.println(
            "SUCCESS: Google search results are visible!"
        );

        System.out.println(
            "Current URL: " + driver.getCurrentUrl()
        );

        // No driver.quit() or driver.close()
        // Browser remains open for manual use
    }
}
