package com.itm.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.itm.base.BaseTest;
import com.itm.pages.CartPage;
import com.itm.pages.CheckoutCompletePage;
import com.itm.pages.CheckoutOverviewPage;
import com.itm.pages.CheckoutPage;
import com.itm.pages.InventoryPage;
import com.itm.pages.LoginPage;
import com.itm.utils.ScreenshotUtil;

/** Full end-to-end journey: login -> products -> cart -> checkout -> confirmation. */
public class CheckoutTest extends BaseTest {

    private void screenshot(String stage) {
        ScreenshotUtil.captureScreenshot(driver, "Checkout_" + stage);
    }

    @Test
    public void verifyCheckoutFlow() {
        // 1. Log in and inspect the Products page.
        InventoryPage products = new LoginPage(driver)
                .login("standard_user", "secret_sauce");
        Assert.assertTrue(products.isLoaded(), "Products page did not load");
        Assert.assertTrue(products.isInventoryVisible(), "Products list is not visible");
        screenshot("01_Products");

        // 2. Capture a before picture and verify the Add button really updates the cart.
        screenshot("02_BeforeAddToCart");
        products.addBackpackToCart();
        Assert.assertEquals(products.getCartBadgeCount(), "1",
                "Cart badge should show one product");
        screenshot("02_ProductAdded");

        // 3. Cart page: verify product and quantity.
        CartPage cart = products.goToCart();
        Assert.assertTrue(cart.isLoaded(), "Cart page not loaded");
        screenshot("03_Cart"); // Captured even if the next assertion fails.
        Assert.assertTrue(cart.hasBackpack(),
                "Sauce Labs Backpack is missing from the cart. "
                + "Open screenshots/Checkout_03_Cart_*.png to inspect the cart.");
        Assert.assertEquals(cart.getBackpackQuantity(), "1",
                "Cart item quantity should be one");

        // 4. Checkout: Your Information.
        CheckoutPage information = cart.clickCheckout();
        Assert.assertTrue(information.isLoaded(),
                "Checkout customer-information page did not load");
        screenshot("04_CheckoutInformation");

        // 5. Complete the required information form and continue.
        CheckoutOverviewPage overview = information.enterCustomerDetails(
                "Test", "Student", "400706");
        Assert.assertTrue(overview.isLoaded(), "Checkout overview page did not load");
        screenshot("05_CheckoutOverview");
        Assert.assertTrue(overview.hasBackpack(),
                "Backpack is not displayed on the checkout overview");
        Assert.assertTrue(overview.getSubtotalText().contains("$"),
                "Item subtotal is not visible");
        Assert.assertTrue(overview.getTaxText().contains("$"),
                "Tax amount is not visible");
        Assert.assertTrue(overview.getTotalText().contains("$"),
                "Order total is not visible");

        // 6. Finish the purchase and verify the confirmation.
        CheckoutCompletePage confirmation = overview.finishOrder();
        Assert.assertTrue(confirmation.isLoaded(),
                "Order completion page did not load");
        Assert.assertEquals(confirmation.getConfirmationText(),
                "Thank you for your order!", "Incorrect order confirmation");
        screenshot("06_OrderComplete");

        // 7. Back Home returns to Products and the cart is empty.
        InventoryPage home = confirmation.backToProducts();
        Assert.assertTrue(home.isLoaded(), "Back Home did not return to Products");
        Assert.assertTrue(home.isCartBadgeAbsent(),
                "Cart badge should be absent after a completed purchase");
        screenshot("07_BackHome");
    }
}
