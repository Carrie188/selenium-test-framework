package com.carrie188.automation.ui.pages;

import com.carrie188.automation.ui.BasePage;
import org.openqa.selenium.WebDriver;

public final class SeleniumHomePage extends BasePage {
    public SeleniumHomePage(WebDriver driver) {
        super(driver);
    }

    public SeleniumHomePage open(String baseUrl) {
        driver.get(baseUrl);
        return this;
    }

    public String pageTitle() {
        return driver.getTitle();
    }
}
