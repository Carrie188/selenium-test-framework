package com.carrie188.automation.ui.tests;

import com.carrie188.automation.config.TestSettings;
import com.carrie188.automation.driver.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

abstract class BaseUiTest {
    protected WebDriver driver;
    protected TestSettings settings;

    @BeforeEach
    void startBrowser() {
        settings = TestSettings.fromSystemProperties();
        driver = DriverFactory.create(settings);
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
