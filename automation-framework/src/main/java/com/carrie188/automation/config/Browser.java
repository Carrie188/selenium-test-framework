package com.carrie188.automation.config;

import java.util.Locale;

public enum Browser {
    CHROME,
    FIREFOX;

    public static Browser from(String value) {
        try {
            return Browser.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported browser: " + value, exception);
        }
    }
}
