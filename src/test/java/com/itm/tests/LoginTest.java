package com.itm.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.itm.base.BaseTest;
import com.itm.pages.InventoryPage;
import com.itm.pages.LoginPage;

/** The seven login checks completed in steps 8-13, rewritten using POM. */
public class LoginTest extends BaseTest {
    private static final String INVALID_ERROR =
            "Epic sadface: Username and password do not match any user in this service";

    private void verifyRejectedLogin(String user, String password, String error) {
        LoginPage page = new LoginPage(driver);
        page.loginExpectingError(user, password);
        Assert.assertEquals(page.getErrorMessage(), error, "Incorrect error message");
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory.html"),
                "Invalid login must not reach the inventory page");
        Assert.assertTrue(page.isLoginFormVisible(), "Login form must remain visible");
    }

    @Test(priority = 1)
    public void validLoginTest() {
        InventoryPage products = new LoginPage(driver)
                .login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),
                "Inventory URL was not reached");
        Assert.assertEquals(products.getPageTitle(), "Products");
        Assert.assertTrue(products.isInventoryVisible(), "Inventory is not visible");
        Assert.assertTrue(products.isLoaded(), "Products page did not load correctly");
    }

    @Test(priority = 2)
    public void invalidPasswordTest() {
        verifyRejectedLogin("standard_user", "wrongpassword", INVALID_ERROR);
    }

    @Test(priority = 3)
    public void emptyUsernameTest() {
        verifyRejectedLogin("", "secret_sauce", "Epic sadface: Username is required");
    }

    @Test(priority = 4)
    public void emptyPasswordTest() {
        verifyRejectedLogin("standard_user", "", "Epic sadface: Password is required");
    }

    @Test(priority = 5)
    public void lockedUserTest() {
        verifyRejectedLogin("locked_out_user", "secret_sauce",
                "Epic sadface: Sorry, this user has been locked out.");
    }

    @Test(priority = 6)
    public void passwordMaskingTest() {
        LoginPage page = new LoginPage(driver);
        page.typePasswordOnly("secret_sauce");
        Assert.assertTrue(page.isPasswordMasked(), "Password input must be masked");
    }

    @Test(priority = 7)
    public void negativeLoginTest() {
        verifyRejectedLogin("invalid_user", "wrong_password", INVALID_ERROR);
    }
}
