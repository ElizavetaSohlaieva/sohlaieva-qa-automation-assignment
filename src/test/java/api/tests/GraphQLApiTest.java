package api.tests;

import api.helpers.ApiHelper;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
public class GraphQLApiTest {

    private static final String EXISTING_MOVIE_ID = "clq3uswv1001q0blchscxb6ba";
    private static final String INVALID_MOVIE_ID = "99999";
    // POSITIVE TESTS

    @Test
    void getListWithPagination() {
        String query = """
                    {
                      movies(first: 3, skip: 1) {
                        id
                        title
                      }
                    }
                """;
        Map<String, Object> requestQuery = Map.of("query", query);

        var jsonPath = given()
                .spec(ApiHelper.graphqlRequestSpec())
                .body(requestQuery)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(jsonPath.getInt("data.movies.size()"))
                .as("The number of returned movies must exactly match the pagination 'first' limit")
                .isEqualTo(3);
    }

    @Test
    void getEntityById() {
        String query = String.format("""
                    {
                      movie(where: {id: "%s"}) {
                        id
                      }
                    }
                """, EXISTING_MOVIE_ID);
        Map<String, Object> requestQuery = Map.of("query", query);
        var jsonPath = given()
                .spec(ApiHelper.graphqlRequestSpec())
                .body(requestQuery)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(jsonPath.getString("data.movie.id"))
                .as("The returned movie ID must match the requested EXISTING_MOVIE_ID")
                .isEqualTo(EXISTING_MOVIE_ID);
    }

    @Test
    void getQueryWithVariables() {
        String query = """
                    query MovieById($id: ID!) {
                        movie(where: {id: $id}) {
                          id
                      }
                    }
                """;

        Map<String, Object> requestQuery = Map.of(
                "query", query,
                "variables", Map.of("id", EXISTING_MOVIE_ID));
        var jsonPath = given()
                .spec(ApiHelper.graphqlRequestSpec())
                .body(requestQuery)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(jsonPath.getString("data.movie.id"))
                .as("The returned movie ID must exactly match the 'id' variable passed in the request")
                .isEqualTo(EXISTING_MOVIE_ID);
    }

    @Test
    void getQueryWithFragment() {
        String query = """
                    fragment PosterInfo on Asset {
                       id
                       url
                     }
                
                     {
                       movies(first: 1, where: { moviePoster: { id_not: null } }) {
                         title
                         moviePoster {
                           ...PosterInfo
                         }
                       }
                     }
                """;
        Map<String, Object> requestQuery = Map.of("query", query);
        var jsonPath = given()
                .spec(ApiHelper.graphqlRequestSpec())
                .body(requestQuery)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        String posterId = jsonPath.getString("data.movies[0].moviePoster.id");
        String posterUrl = jsonPath.getString("data.movies[0].moviePoster.url");

        assertThat(posterId)
                .as("ID is empty or null")
                .isNotNull()
                .isNotEmpty();

        assertThat(posterUrl)
                .as("URL should start with http")
                .isNotNull()
                .startsWith("http");
    }

    // NEGATIVE TESTS

    @Test
    void getInvalidId() {
        String query = String.format("""
                    {
                      movie(where:{id: "%s"}) {
                        id
                        title
                      }
                    }
                """, INVALID_MOVIE_ID);
        Map<String, Object> requestQuery = Map.of("query", query);
        var jsonPath = given()
                .spec(ApiHelper.graphqlRequestSpec())
                .body(requestQuery)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(jsonPath.getString("data.movie"))
                .as("The 'movie' object should be null when querying with a non-existent ID")
                .isNull();
    }

    @Test
    void getMalformedQuery() {
        String query = """
                    { movie( { title } }
                """;
        Map<String, Object> requestQuery = Map.of("query", query);
        var jsonPath = given()
                .spec(ApiHelper.graphqlRequestSpec())
                .body(requestQuery)
                .when()
                .post()
                .then()
                .statusCode(400)
                .extract()
                .jsonPath();
        List<Object> errors = jsonPath.getList("errors");
        assertThat(errors)
                .as("The 'errors' array must be present in the root")
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getNonExistentField() {
        String query = String.format("""
                    {
                      movie(where: {id: "%s"}) {
                        notExistedField
                      }
                    }
                """, EXISTING_MOVIE_ID);
        Map<String, Object> requestQuery = Map.of("query", query);
        var jsonPath = given()
                .spec(ApiHelper.graphqlRequestSpec())
                .body(requestQuery)
                .when()
                .post()
                .then()
                .statusCode(400)
                .extract()
                .jsonPath();
        List<String> errors = jsonPath.getList("errors.message");
        assertThat(errors.getFirst())
                .as("The error message should explicitly mention the invalid field name")
                .contains("notExistedField")
                .isNotNull();
    }
}