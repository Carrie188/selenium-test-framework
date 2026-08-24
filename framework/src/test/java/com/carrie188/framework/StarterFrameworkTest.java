package com.carrie188.framework;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StarterFrameworkTest {
  private WebDriver driver;

  @Test
  void apiHealthCheck() {
    RestAssured.baseURI = System.getProperty("api.base.url", "https://httpbin.org");
    given().when().get("/get").then().statusCode(200);
  }

  @Test
  void seleniumHomePageLoads() {
    ChromeOptions options = new ChromeOptions();
    if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
      options.addArguments("--headless=new");
    }
    driver = new ChromeDriver(options);
    driver.get(System.getProperty("base.url", "https://www.selenium.dev/"));
    assertTrue(driver.getTitle().contains("Selenium"));
  }

  @AfterEach
  void closeBrowser() {
    if (driver != null) driver.quit();
  }
}
