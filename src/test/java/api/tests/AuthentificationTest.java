package api.tests;

import api.helpers.ApiHelper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static config.TestConfig.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthentificationTest {
    @Test
    void shouldGenerateAuthTokenWithValidCredentials() {

        Map<String, String> credentials =
                ApiHelper.credentials(
                        USERNAME,
                        PASSWORD
                );

        String token = given()
                .spec(ApiHelper.requestSpec())
                .body(credentials)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        assertThat(token)
                .as("Token was generated correctly")
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void shouldNotGenerateTokenWithInvalidCredentials() {

        Map<String, String> credentials =
                ApiHelper.credentials(
                        "",
                        ""
                );

        String token = given()
                .spec(ApiHelper.requestSpec())
                .body(credentials)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("reason");

        assertThat(token)
                .as("Token was not generated correctly")
                .isEqualTo("Bad credentials");
    }
}
