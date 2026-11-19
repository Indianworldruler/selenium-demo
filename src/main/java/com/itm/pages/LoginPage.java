package com.itm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.itm.utils.DemoActions;
import com.itm.utils.WaitUtil;

/** Locators, waits and interactions for the SauceDemo login screen. */
public class LoginPage {
    private final WebDriver driver;
    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    private void enterAndSubmit(String user, String pass) {
        DemoActions.type(driver, WaitUtil.visible(driver, username), user);
        DemoActions.type(driver, WaitUtil.visible(driver, password), pass);
        DemoActions.click(driver, WaitUtil.clickable(driver, loginButton));
    }

    public InventoryPage login(String user, String pass) {
        enterAndSubmit(user, pass);
        WaitUtil.urlContains(driver, "inventory.html");
        return new InventoryPage(driver);
    }

    public void loginExpectingError(String user, String pass) {
        enterAndSubmit(user, pass);
        WaitUtil.visible(driver, errorMessage);
    }

    public String getErrorMessage() {
        WebElement error = WaitUtil.visible(driver, errorMessage);
        DemoActions.highlight(driver, error);
        return error.getText();
    }

    public boolean isLoginFormVisible() {
        return WaitUtil.visible(driver, username).isDisplayed()
                && WaitUtil.visible(driver, loginButton).isDisplayed();
    }

    public void typePasswordOnly(String value) {
        DemoActions.type(driver, WaitUtil.visible(driver, password), value);
    }

    public boolean isPasswordMasked() {
        return "password".equals(WaitUtil.visible(driver, password).getDomAttribute("type"));
    }
}
