package api.tests;

import api.helpers.ApiHelper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static config.TestConfig.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AuthentificationTest {
    @Test
    void shouldGenerateAuthTokenWithValidCredentials() {

        Map<String, String> credentials =
                ApiHelper.credentials(
                        USERNAME,
                        PASSWORD
                );

        given()
                .spec(ApiHelper.requestSpec())
                .body(credentials)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }

    @Test
    void shouldNotGenerateTokenWithInvalidCredentials() {

        Map<String, String> credentials =
                ApiHelper.credentials(
                        "",
                        ""
                );

        given()
                .spec(ApiHelper.requestSpec())
                .body(credentials)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }
}
