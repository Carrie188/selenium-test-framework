package com.carrie188.automation.driver;

import com.carrie188.automation.config.TestSettings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver create(TestSettings settings) {
        return switch (settings.browser()) {
            case CHROME -> new ChromeDriver(chromeOptions(settings));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(settings));
        };
    }

    private static ChromeOptions chromeOptions(TestSettings settings) {
        ChromeOptions options = new ChromeOptions();
        if (settings.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900");
        return options;
    }

    private static FirefoxOptions firefoxOptions(TestSettings settings) {
        FirefoxOptions options = new FirefoxOptions();
        if (settings.headless()) {
            options.addArguments("-headless");
        }
        return options;
    }
}
