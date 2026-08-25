package com.carrie188.automation.config;

public record TestSettings(String baseUrl, String apiBaseUrl, Browser browser, boolean headless) {
    private static final String DEFAULT_BASE_URL = "https://www.selenium.dev/";
    private static final String DEFAULT_API_BASE_URL = "https://httpbin.org";

    public static TestSettings fromSystemProperties() {
        return new TestSettings(
            propertyOrDefault("base.url", DEFAULT_BASE_URL),
            propertyOrDefault("api.base.url", DEFAULT_API_BASE_URL),
            Browser.from(propertyOrDefault("browser", "chrome")),
            Boolean.parseBoolean(propertyOrDefault("headless", "true"))
        );
    }

    private static String propertyOrDefault(String key, String defaultValue) {
        String value = System.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
