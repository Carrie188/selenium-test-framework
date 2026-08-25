package com.carrie188.automation.config;

import com.carrie188.automation.api.ApiSpecifications;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestSettingsTest {
    @AfterEach
    void clearProperties() {
        System.clearProperty("base.url");
        System.clearProperty("api.base.url");
        System.clearProperty("browser");
        System.clearProperty("headless");
    }

    @Test
    void usesStableDefaultsWhenPropertiesAreAbsent() {
        TestSettings settings = TestSettings.fromSystemProperties();

        assertEquals("https://www.selenium.dev/", settings.baseUrl());
        assertEquals("https://httpbin.org", settings.apiBaseUrl());
        assertEquals(Browser.CHROME, settings.browser());
        assertTrue(settings.headless());
    }

    @Test
    void readsExplicitRuntimeOverrides() {
        System.setProperty("browser", "firefox");
        System.setProperty("headless", "false");
        System.setProperty("base.url", "https://ui.example.test");
        System.setProperty("api.base.url", "https://api.example.test");

        TestSettings settings = TestSettings.fromSystemProperties();

        assertEquals(Browser.FIREFOX, settings.browser());
        assertFalse(settings.headless());
        assertEquals("https://ui.example.test", settings.baseUrl());
        assertEquals("https://api.example.test", settings.apiBaseUrl());
    }

    @Test
    void sendsRequestsToConfiguredApiBaseUrl() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/health", exchange -> {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        });
        server.start();

        try {
            String apiBaseUrl = "http://localhost:" + server.getAddress().getPort();
            given()
                .spec(ApiSpecifications.request(new TestSettings("https://ui.example.test", apiBaseUrl, Browser.CHROME, true)))
                .when()
                .get("/health")
                .then()
                .statusCode(204);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void logsApiFailuresWithoutSensitiveHeaders() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/failure", exchange -> {
            exchange.sendResponseHeaders(500, -1);
            exchange.close();
        });
        server.start();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

        try {
            String apiBaseUrl = "http://localhost:" + server.getAddress().getPort();
            given()
                .header("Authorization", "super-secret-token")
                .spec(ApiSpecifications.request(new TestSettings("https://ui.example.test", apiBaseUrl, Browser.CHROME, true)))
                .when()
                .get("/failure");
        } finally {
            System.setOut(originalOut);
            server.stop(0);
        }

        String logOutput = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(logOutput.contains("500"));
        assertFalse(logOutput.contains("super-secret-token"));
    }
}
