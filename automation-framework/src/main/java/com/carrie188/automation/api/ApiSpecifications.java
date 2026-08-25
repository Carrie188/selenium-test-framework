package com.carrie188.automation.api;

import com.carrie188.automation.config.TestSettings;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.specification.RequestSpecification;

public final class ApiSpecifications {
    private ApiSpecifications() {
    }

    public static RequestSpecification request(TestSettings settings) {
        return new RequestSpecBuilder()
            .setBaseUri(settings.apiBaseUrl())
            .setConfig(RestAssured.config().logConfig(
                LogConfig.logConfig().blacklistHeader("Authorization").blacklistHeader("Cookie")
            ))
            .addFilter(new ErrorLoggingFilter())
            .build();
    }
}
