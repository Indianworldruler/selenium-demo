package com.itm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.itm.utils.DemoActions;
import com.itm.utils.WaitUtil;

/** SauceDemo shopping cart. Product presence is asserted before checkout. */
public class CartPage {
    private final WebDriver driver;
    private final By title = By.className("title");
    private final By cartItemNames = By.cssSelector(".cart_item .inventory_item_name");
    private final By cartItemQuantity = By.className("cart_quantity");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("cart.html")
                && "Your Cart".equals(WaitUtil.visible(driver, title).getText());
    }

    public boolean hasBackpack() {
        for (WebElement item : driver.findElements(cartItemNames)) {
            if ("Sauce Labs Backpack".equals(item.getText())) return true;
        }
        return false;
    }

    public String getBackpackQuantity() {
        return WaitUtil.visible(driver, cartItemQuantity).getText();
    }

    public CheckoutPage clickCheckout() {
        DemoActions.click(driver, WaitUtil.clickable(driver, checkoutButton));
        WaitUtil.urlContains(driver, "checkout-step-one.html");
        return new CheckoutPage(driver);
    }
}
