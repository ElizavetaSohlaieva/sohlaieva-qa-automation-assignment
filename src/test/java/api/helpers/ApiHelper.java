package api.helpers;

import config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static config.TestConfig.*;
import static io.restassured.RestAssured.given;

public class ApiHelper {
    public static Map<String, String> credentials(
            String username,
            String password
    ) {
        return Map.of(
                "username", username,
                "password", password
        );
    }

    public static String getToken() {

        Map<String, String> credentials = Map.of(
                "username", USERNAME,
                "password", PASSWORD
        );

        return given()
                .spec(ApiHelper.requestSpec())
                .body(credentials)
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    private static RequestSpecBuilder baseRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(API_BASE_URL)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL);
    }

    public static RequestSpecification requestSpec() {
        return baseRequestSpecBuilder()
                .build();
    }

    public static RequestSpecification authorizedRequestSpec(String token) {
        return baseRequestSpecBuilder()
                .addCookie("token", token)
                .build();
    }

    private static RequestSpecBuilder graphqlRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.GRAPHQL_URL) // Берем URL GraphQL
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL);
    }

    public static RequestSpecification graphqlRequestSpec() {
        return graphqlRequestSpecBuilder().build();
    }
}
