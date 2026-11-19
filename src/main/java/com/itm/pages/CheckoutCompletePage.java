package com.itm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.itm.utils.DemoActions;
import com.itm.utils.WaitUtil;

/** Final order confirmation page. */
public class CheckoutCompletePage {
    private final WebDriver driver;
    private final By title = By.className("title");
    private final By confirmation = By.className("complete-header");
    private final By backHome = By.id("back-to-products");

    public CheckoutCompletePage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("checkout-complete.html")
                && "Checkout: Complete!".equals(WaitUtil.visible(driver, title).getText());
    }

    public String getConfirmationText() {
        return WaitUtil.visible(driver, confirmation).getText();
    }

    public InventoryPage backToProducts() {
        DemoActions.click(driver, WaitUtil.clickable(driver, backHome));
        WaitUtil.urlContains(driver, "inventory.html");
        return new InventoryPage(driver);
    }
}
