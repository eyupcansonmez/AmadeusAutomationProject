package com.amadeus.apiBase;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class ApiTestBase {
    static {
        RestAssured.baseURI = "https://flights-api.buraky.workers.dev/";
    }
}