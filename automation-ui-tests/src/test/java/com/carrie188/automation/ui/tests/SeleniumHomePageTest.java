package com.carrie188.automation.ui.tests;

import com.carrie188.automation.ui.pages.SeleniumHomePage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@Tag("smoke")
class SeleniumHomePageTest extends BaseUiTest {
    @Test
    void displaysTheSeleniumHomePageTitle() {
        SeleniumHomePage homePage = new SeleniumHomePage(driver).open(settings.baseUrl());

        assertTrue(homePage.pageTitle().contains("Selenium"));
    }
}
