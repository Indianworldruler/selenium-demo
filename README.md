# SauceDemo — full checkout verification (fix for CheckoutTest line 24)

This is an update to the earlier Selenium + Java + Maven POM project. It retains all **nine TestNG tests**, the one-browser demo setting, failure screenshots, Extent reporting, logging and original package names. The checkout test now exercises the **entire purchasing flow** through order confirmation and Back Home.

## What caused the earlier failure?

`CheckoutTest.java:24` called `cart.hasBackpack()` before the checkout click. The cart lacked a visible `.inventory_item_name`, resulting in a 15-second timeout. The revised `InventoryPage.addBackpackToCart()` waits until the badge becomes **1** and the button changes to **Remove** before entering the cart; the cart test now captures a screenshot before checking the product. If SauceDemo still fails to add the product, the test *correctly fails early* with a more informative message; it does not falsely pass.

## Verified pages and automatic screenshots

1. Products — `Checkout_01_Products_*.png`
2. Product added — `Checkout_02_ProductAdded_*.png`
3. Your Cart — `Checkout_03_Cart_*.png`
4. Checkout: Your Information — `Checkout_04_CheckoutInformation_*.png`
5. Checkout: Overview — `Checkout_05_CheckoutOverview_*.png`
6. Checkout: Complete! — `Checkout_06_OrderComplete_*.png`
7. Back Home — `Checkout_07_BackHome_*.png`

All screenshots go under `selenium-demo/screenshots/`. The negative/login tests are unchanged.

## Installation on your Mac

1. Make sure the archive is downloaded to your Downloads folder.
2. Open Terminal, run:

```bash
cd "/Users/parthsahani/Desktop/software testing /nov16"
cp -R selenium-demo "selenium-demo-backup-$(date +%Y%m%d-%H%M%S)"
unzip -o ~/Downloads/SauceDemo_Full_Checkout_Fix.zip -d .
cd selenium-demo
mvn -U clean test
```

This replaces only paths included in the archive. Any older root-level `src/test/java/com/itm/LoginTest.java`, `SeleniumTest.java` or old JUnit `AppTest.java` are **not automatically removed**; delete these if they are no longer needed. The provided POM runs the nine specified tests via `testng.xml`.

The POM configures `testng.xml`, so `mvn test` runs all nine scenarios as a single sequential TestNG suite. In VS Code, you can also run `CheckoutTest.verifyCheckoutFlow` directly using **Run Test** if you only want to debug checkout.

To slow the demonstration:

```bash
mvn -Ddemo.typingMs=250 -Ddemo.actionMs=1500 -Ddemo.resultMs=4000 test
```

## Results

- `screenshots/`: seven checkout checkpoint screenshots, plus any on failure
- `reports/ExtentReport.html`: test report
- `logs/automation.log`: activity log
- `target/surefire-reports/`: Maven/TestNG reports

The browser could not be launched inside this container, so the actual SauceDemo UI run must be verified on your Mac. If the cart screenshot shows an empty cart, share the `Checkout_03_Cart` image and the Maven failure text: this will distinguish a website cart-state defect from an automation issue.


## November 16 update: cart badge timeout and Chrome password popup

- `DriverFactory` now turns off Chrome's password-save and password-leak popups **only in the automated test session**.
- `InventoryPage.addBackpackToCart()` waits for **both** the Remove button and cart badge `1`, retries once if the click did not change application state, and saves a screenshot for each failed attempt.
- Failure listener holds Chrome visible for about 12 seconds after failure (configurable with `-Ddemo.failurePauseMs=20000`). Chrome still closes when the suite completes.
- Checkout checkpoints include `Checkout_02_BeforeAddToCart`, `Checkout_02_ProductAdded`, Cart, customer information, overview, completed order, and return to products.

### Replace files in your existing project
From `/Users/parthsahani/Desktop/software testing /nov16`, back up `selenium-demo`, then extract this zip with `unzip -o ~/Downloads/SauceDemo_Checkout_Popup_Cart_Fix.zip -d .`. The archive contains `selenium-demo/` as its top-level folder. Do not extract while the tests are running.

### Verify checkout alone (recommended first)
`cd "/Users/parthsahani/Desktop/software testing /nov16/selenium-demo"`
`mvn -Dtest=CheckoutTest test`

### Then run the entire suite
`mvn clean test`

If checkout still fails, send screenshot `screenshots/Checkout_AddToCart_Attempt2_*.png` and the `[CHECKOUT]` console lines. A browser cannot be tested in this packaging environment; these must be executed on your Mac to confirm.
