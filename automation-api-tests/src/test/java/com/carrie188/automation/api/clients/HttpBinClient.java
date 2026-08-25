package com.carrie188.automation.api.clients;

import com.carrie188.automation.api.ApiSpecifications;
import com.carrie188.automation.config.TestSettings;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Minimal example of an endpoint client. Add business-specific API clients beside this class.
 */
public final class HttpBinClient {
    private final RequestSpecification request;

    public HttpBinClient(TestSettings settings) {
        request = ApiSpecifications.request(settings);
    }

    public Response get() {
        return RestAssured.given()
                .spec(request)
                .when()
                .get("/get");
    }
}
