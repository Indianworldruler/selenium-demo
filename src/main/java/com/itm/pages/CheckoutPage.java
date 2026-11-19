package com.itm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.itm.utils.DemoActions;
import com.itm.utils.WaitUtil;

/** Checkout step one: customer information. */
public class CheckoutPage {
    private final WebDriver driver;
    private final By title = By.className("title");
    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.id("continue");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("checkout-step-one.html")
                && "Checkout: Your Information".equals(
                        WaitUtil.visible(driver, title).getText())
                && WaitUtil.visible(driver, firstName).isDisplayed();
    }

    public CheckoutOverviewPage enterCustomerDetails(
            String first, String last, String zip) {
        DemoActions.type(driver, WaitUtil.visible(driver, firstName), first);
        DemoActions.type(driver, WaitUtil.visible(driver, lastName), last);
        DemoActions.type(driver, WaitUtil.visible(driver, postalCode), zip);
        DemoActions.click(driver, WaitUtil.clickable(driver, continueButton));
        WaitUtil.urlContains(driver, "checkout-step-two.html");
        return new CheckoutOverviewPage(driver);
    }
}
