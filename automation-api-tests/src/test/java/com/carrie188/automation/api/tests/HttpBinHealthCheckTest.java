package com.carrie188.automation.api.tests;

import com.carrie188.automation.api.clients.HttpBinClient;
import com.carrie188.automation.config.TestSettings;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("api")
@Tag("smoke")
class HttpBinHealthCheckTest {

    @Test
    void getsAHealthyResponseFromTheConfiguredApi() {
        Response response = new HttpBinClient(TestSettings.fromSystemProperties()).get();

        assertEquals(200, response.statusCode());
    }
}
