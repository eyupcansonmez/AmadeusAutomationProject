package com.amadeus.page;

import com.amadeus.apiBase.ApiTestBase;
import com.thoughtworks.gauge.Step;
import io.restassured.response.Response;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApiSteps extends ApiTestBase {
    private static final Logger logger = LoggerFactory.getLogger(ApiSteps.class);
    @Step("GET isteği ile <endpoint> adresinden 200 status code alındığını doğrula")
    public void verifyStatusCode(String endpoint) {
        Response response = given().when().get(endpoint).then().extract().response();
        int statusCode = response.getStatusCode();
        logger.info(endpoint + " adresine yapılan GET isteği ile alınan status kodu: " + statusCode);
        assertEquals(200, statusCode, "Status code beklenen değerden farklı.");
    }

    @Step("<endpoint> adresinden gelen cevabın yapısını doğrula")
    public void verifyResponseStructure(String endpoint) {
        Response response = given().when().get(endpoint).then().extract().response();
        logger.info(endpoint + " adresinden gelen cevap: " + response.asString());

        assertAll("Response validation",
                () -> assertNotNull(response.jsonPath().getInt("data[0].id"), "ID null olamaz."),
                () -> assertNotNull(response.jsonPath().getString("data[0].from"), "From null olamaz."),
                () -> assertNotNull(response.jsonPath().getString("data[0].to"), "To null olamaz."),
                () -> assertNotNull(response.jsonPath().getString("data[0].date"), "Date null olamaz.")
        );
    }

    @Step("<endpoint> adresinden gelen cevabın Content-Type header'ını doğrula")
    public void verifyContentTypeHeader(String endpoint) {
        Response response = given().when().get(endpoint).then().extract().response();
        String contentType = response.getHeader("Content-Type");
        logger.info(endpoint + " adresinden gelen cevabın Content-Type header'ı: " + contentType);
        assertEquals("application/json", contentType, "Content-Type header'ı beklenen değerden farklı.");
    }

}
