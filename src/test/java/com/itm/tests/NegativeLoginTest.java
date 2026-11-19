package com.itm.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.itm.base.BaseTest;
import com.itm.pages.LoginPage;

/** Separate POM test class required by step 21 of the PDF. */
public class NegativeLoginTest extends BaseTest {

    @Test
    public void verifyInvalidLogin() {
        LoginPage page = new LoginPage(driver);
        page.loginExpectingError("wrongUser", "wrongPassword");
        Assert.assertEquals(page.getErrorMessage(),
                "Epic sadface: Username and password do not match any user in this service");
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory.html"));
        Assert.assertTrue(page.isLoginFormVisible(), "Login form should remain present");
    }
}
