package com.itm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.itm.utils.DemoActions;
import com.itm.utils.WaitUtil;

/** Checkout step two: product and order price review. */
public class CheckoutOverviewPage {
    private final WebDriver driver;
    private final By title = By.className("title");
    private final By itemNames = By.cssSelector(".cart_item .inventory_item_name");
    private final By subtotal = By.className("summary_subtotal_label");
    private final By tax = By.className("summary_tax_label");
    private final By total = By.className("summary_total_label");
    private final By finishButton = By.id("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("checkout-step-two.html")
                && "Checkout: Overview".equals(WaitUtil.visible(driver, title).getText());
    }

    public boolean hasBackpack() {
        for (WebElement item : driver.findElements(itemNames)) {
            if ("Sauce Labs Backpack".equals(item.getText())) return true;
        }
        return false;
    }

    public String getSubtotalText() {
        return WaitUtil.visible(driver, subtotal).getText();
    }

    public String getTaxText() {
        return WaitUtil.visible(driver, tax).getText();
    }

    public String getTotalText() {
        return WaitUtil.visible(driver, total).getText();
    }

    public CheckoutCompletePage finishOrder() {
        DemoActions.click(driver, WaitUtil.clickable(driver, finishButton));
        WaitUtil.urlContains(driver, "checkout-complete.html");
        return new CheckoutCompletePage(driver);
    }
}
