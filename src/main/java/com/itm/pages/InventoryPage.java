package com.itm.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.itm.utils.DemoActions;
import com.itm.utils.ScreenshotUtil;
import com.itm.utils.WaitUtil;

/** SauceDemo Products page: verifies the actual cart state after clicking. */
public class InventoryPage {
    private final WebDriver driver;
    private final By title = By.className("title");
    private final By inventoryList = By.className("inventory_list");
    private final By backpackButton = By.id("add-to-cart-sauce-labs-backpack");
    private final By removeBackpackButton = By.id("remove-sauce-labs-backpack");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getPageTitle() {
        return WaitUtil.visible(driver, title).getText();
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("inventory.html")
                && "Products".equals(getPageTitle());
    }

    public boolean isInventoryVisible() {
        return WaitUtil.visible(driver, inventoryList).isDisplayed();
    }

    /** The page must show Remove and a badge of 1 before checkout proceeds. */
    private boolean backpackIsAdded() {
        List<WebElement> badges = driver.findElements(cartBadge);
        List<WebElement> removeButtons = driver.findElements(removeBackpackButton);
        return !removeButtons.isEmpty() && !badges.isEmpty()
                && "1".equals(badges.get(0).getText());
    }

    /**
     * Check the application's response to the click; retry once only if
     * the Add-to-cart button has not actually updated the cart.
     * A screenshot and a meaningful failure are provided if it still fails.
     */
    public void addBackpackToCart() {
        if (!isLoaded() || !isInventoryVisible()) {
            throw new AssertionError("Products page was not ready before adding an item");
        }

        if (backpackIsAdded()) return;

        for (int attempt = 1; attempt <= 2; attempt++) {
            System.out.println("[CHECKOUT] Add to cart attempt " + attempt);
            DemoActions.click(driver, WaitUtil.clickable(driver, backpackButton));

            try {
                new WebDriverWait(driver, Duration.ofSeconds(6))
                        .until(d -> backpackIsAdded() ? Boolean.TRUE : null);
                System.out.println("[CHECKOUT] Cart updated: Remove button present, badge = 1");
                return;
            } catch (TimeoutException ex) {
                try {
                    System.out.println("[CHECKOUT] Screenshot: "
                            + ScreenshotUtil.captureScreenshot(driver,
                                    "Checkout_AddToCart_Attempt" + attempt));
                } catch (RuntimeException screenshotError) {
                    System.out.println("[CHECKOUT] Screenshot unavailable: "
                            + screenshotError.getMessage());
                }
                System.out.println("[CHECKOUT] Cart did not change after attempt " + attempt
                        + "; current page: " + driver.getCurrentUrl());
                if (attempt < 2) DemoActions.pause(1000);
            }
        }

        throw new AssertionError(
                "Add to cart did not update SauceDemo. Expected button to change to "
                + "'Remove' and cart badge to show 1. Review screenshots/Checkout_AddToCart_*.png "
                + "and check whether the site displays an overlay or JavaScript error.");
    }

    public String getCartBadgeCount() {
        return WaitUtil.visible(driver, cartBadge).getText();
    }

    public boolean isCartBadgeAbsent() {
        return driver.findElements(cartBadge).isEmpty();
    }

    public CartPage goToCart() {
        DemoActions.click(driver, WaitUtil.clickable(driver, cartLink));
        WaitUtil.urlContains(driver, "cart.html");
        return new CartPage(driver);
    }
}
