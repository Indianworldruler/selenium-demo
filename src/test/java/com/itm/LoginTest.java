
package com.itm;

import java.time.Duration;
import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String URL = "https://www.saucedemo.com/";

    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage =
        By.cssSelector("h3[data-test='error']");

    // ==========================================
    // VISIBILITY SETTINGS
    // ==========================================

    private final int TYPING_DELAY = 180;
    private final int ACTION_DELAY = 1200;
    private final int RESULT_DELAY = 3000;

    // Pause for visual demonstration
    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                "Test interrupted", e
            );
        }
    }

    // Type text slowly, character by character
    private void slowType(WebElement element, String text) {

        for (char character : text.toCharArray()) {
            element.sendKeys(String.valueOf(character));
            pause(TYPING_DELAY);
        }
    }

    // Highlight an element for better visibility
    private void highlight(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].style.outline='3px solid orange';",
            element
        );

        pause(500);

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].style.outline='';",
            element
        );
    }

    // ==========================================
    // BROWSER SETUP
    // ==========================================

    @BeforeClass
    public void setup() {

        System.out.println(
            "\n========== STARTING LOGIN TEST SUITE =========="
        );

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
            driver,
            Duration.ofSeconds(15)
        );

        driver.get(URL);

        pause(2000);
    }

    // Reset page before every test
    @BeforeMethod
    public void openLoginPage(Method method) {

        System.out.println(
            "\nRunning Test: " + method.getName()
        );

        driver.get(URL);

        ((JavascriptExecutor) driver).executeScript(
            "window.localStorage.clear();" +
            "window.sessionStorage.clear();"
        );

        driver.manage().deleteAllCookies();

        driver.navigate().refresh();

        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                username
            )
        );

        pause(1500);
    }

    // ==========================================
    // REUSABLE LOGIN METHOD
    // ==========================================

    private void login(String user, String pass) {

        // Step 1: Enter username
        WebElement usernameField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                username
            )
        );

        highlight(usernameField);

        usernameField.clear();

        slowType(usernameField, user);

        pause(ACTION_DELAY);

        // Step 2: Enter password
        WebElement passwordField = driver.findElement(
            password
        );

        highlight(passwordField);

        passwordField.clear();

        slowType(passwordField, pass);

        pause(ACTION_DELAY);

        // Step 3: Click login button
        WebElement button = wait.until(
            ExpectedConditions.elementToBeClickable(
                loginButton
            )
        );

        highlight(button);

        pause(ACTION_DELAY);

        button.click();

        // Allow result to remain visible
        pause(1500);
    }

    // ==========================================
    // REUSABLE ERROR ASSERTIONS
    // ==========================================

    private void verifyError(String expectedMessage) {

        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                errorMessage
            )
        );

        highlight(error);

        // Assertion 1: Error message
        Assert.assertEquals(
            error.getText(),
            expectedMessage,
            "Incorrect error message displayed"
        );

        // Assertion 2: Products page inaccessible
        Assert.assertFalse(
            driver.getCurrentUrl().contains(
                "inventory.html"
            ),
            "User should not reach Products page"
        );

        // Assertion 3: Login form remains visible
        Assert.assertTrue(
            driver.findElement(
                loginButton
            ).isDisplayed(),
            "Login form should remain visible"
        );
    }

    // ==========================================
    // TC01: VALID LOGIN
    // ==========================================

    @Test(priority = 1)
    public void validLoginTest() {

        login("standard_user", "secret_sauce");

        // Explicit wait for successful navigation
        wait.until(
            ExpectedConditions.urlContains(
                "inventory.html"
            )
        );

        // Assertion 1: Correct URL
        Assert.assertTrue(
            driver.getCurrentUrl().contains(
                "inventory.html"
            ),
            "Inventory page not loaded"
        );

        // Assertion 2: Correct Products title
        WebElement titleElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.className("title")
            )
        );

        highlight(titleElement);

        String pageTitle = titleElement.getText();

        Assert.assertEquals(
            pageTitle,
            "Products",
            "Incorrect page title"
        );

        // Assertion 3: Product inventory visible
        WebElement inventory = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.className("inventory_list")
            )
        );

        Assert.assertTrue(
            inventory.isDisplayed(),
            "Product inventory not visible"
        );

        System.out.println(
            "TC01 PASSED: Successful login"
        );
    }

    // ==========================================
    // TC02: INVALID PASSWORD
    // ==========================================

    @Test(priority = 2)
    public void invalidPasswordTest() {

        login("standard_user", "wrongpassword");

        verifyError(
            "Epic sadface: Username and password do not match any user in this service"
        );

        System.out.println(
            "TC02 PASSED: Invalid password rejected"
        );
    }

    // ==========================================
    // TC03: EMPTY USERNAME
    // ==========================================

    @Test(priority = 3)
    public void emptyUsernameTest() {

        login("", "secret_sauce");

        verifyError(
            "Epic sadface: Username is required"
        );

        System.out.println(
            "TC03 PASSED: Empty username rejected"
        );
    }

    // ==========================================
    // TC04: EMPTY PASSWORD
    // ==========================================

    @Test(priority = 4)
    public void emptyPasswordTest() {

        login("standard_user", "");

        verifyError(
            "Epic sadface: Password is required"
        );

        System.out.println(
            "TC04 PASSED: Empty password rejected"
        );
    }

    // ==========================================
    // TC05: LOCKED ACCOUNT
    // ==========================================

    @Test(priority = 5)
    public void lockedUserTest() {

        login("locked_out_user", "secret_sauce");

        verifyError(
            "Epic sadface: Sorry, this user has been locked out."
        );

        System.out.println(
            "TC05 PASSED: Locked account rejected"
        );
    }

    // ==========================================
    // TC06: PASSWORD MASKING
    // ==========================================

    @Test(priority = 6)
    public void passwordMaskingTest() {

        WebElement passwordField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                password
            )
        );

        highlight(passwordField);

        slowType(
            passwordField,
            "secret_sauce"
        );

        pause(ACTION_DELAY);

        // Verify password input type
        Assert.assertEquals(
            passwordField.getDomAttribute("type"),
            "password",
            "Password field is not masked"
        );

        System.out.println(
            "TC06 PASSED: Password masking verified"
        );
    }

    // ==========================================
    // TC07: NEGATIVE LOGIN
    // ==========================================

    @Test(priority = 7)
    public void negativeLoginTest() {

        login("invalid_user", "wrong_password");

        // Assertion 1: Correct error message
        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                errorMessage
            )
        );

        highlight(error);

        Assert.assertEquals(
            error.getText(),
            "Epic sadface: Username and password do not match any user in this service",
            "Incorrect login error message"
        );

        // Assertion 2: Inventory page not accessible
        Assert.assertFalse(
            driver.getCurrentUrl().contains(
                "inventory.html"
            ),
            "Invalid user should not reach Products page"
        );

        // Assertion 3: Login button still visible
        Assert.assertTrue(
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    loginButton
                )
            ).isDisplayed(),
            "Login button should remain visible"
        );

        // Assertion 4: Username still visible
        Assert.assertTrue(
            driver.findElement(
                username
            ).isDisplayed(),
            "Username field should remain visible"
        );

        System.out.println(
            "TC07 PASSED: Negative login verified"
        );
    }

    // ==========================================
    // AFTER EACH TEST
    // ==========================================

    @AfterMethod(alwaysRun = true)
    public void afterEachTest(ITestResult result) {

        if (result.getStatus() == ITestResult.SUCCESS) {

            System.out.println(
                "RESULT: PASSED - " +
                result.getName()
            );

        } else if (result.getStatus() == ITestResult.FAILURE) {

            System.out.println(
                "RESULT: FAILED - " +
                result.getName()
            );

        } else {

            System.out.println(
                "RESULT: SKIPPED - " +
                result.getName()
            );
        }

        // Keep result visible before next test
        pause(RESULT_DELAY);
    }

    // ==========================================
    // CLOSE CHROME AFTER ALL TESTS
    // ==========================================

    @AfterClass(alwaysRun = true)
    public void tearDown() {

        System.out.println(
            "\n========== LOGIN TEST SUITE COMPLETED =========="
        );

        pause(3000);

        if (driver != null) {
            driver.quit();
        }
    }
}
