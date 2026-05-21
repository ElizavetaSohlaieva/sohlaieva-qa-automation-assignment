package api.tests;

import api.helpers.ApiHelper;
import api.models.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Tag("api")
public class RestBookingApiTest {
    private static final int NON_EXISTING_BOOKING_ID = 99999999;

    private Booking createTestBooking() {
        return Booking.builder()
                .firstname("John")
                .lastname("Doe")
                .totalprice(120)
                .depositpaid(true)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-01-01")
                                .checkout("2025-01-10")
                                .build()
                )
                .additionalneeds("Breakfast")
                .build();
    }

    private int createBooking() {
        return given()
                .spec(ApiHelper.requestSpec())
                .body(createTestBooking())
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .path("bookingid");
    }

    // CRUD

    @Test
    void shouldCreateBooking() {

        int id = createBooking();

        assertThat(id).isPositive();
    }

    @Test
    void shouldGetBookingById() {

        int id = createBooking();

        Booking response =
                given()
                        .spec(ApiHelper.requestSpec())
                        .when()
                        .get("/booking/" + id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Booking.class);

        assertThat(response.getFirstname()).isEqualTo("John");
        assertThat(response.getLastname()).isEqualTo("Doe");
        assertThat(response.getTotalprice()).isEqualTo(120);
        assertThat(response.isDepositpaid()).isTrue();

        assertThat(response.getBookingdates().getCheckin())
                .matches("\\d{4}-\\d{2}-\\d{2}");

        assertThat(response.getBookingdates().getCheckout())
                .matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Test
    void shouldUpdateBooking() {

        int id = createBooking();
        String token = ApiHelper.getToken();

        Booking updated = Booking.builder()
                .firstname("Jane")
                .lastname("Smith")
                .totalprice(180)
                .depositpaid(false)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-02-01")
                                .checkout("2025-02-10")
                                .build()
                )
                .additionalneeds("Lunch")
                .build();

        Booking response =
                given()
                        .spec(ApiHelper.authorizedRequestSpec(token))
                        .body(updated)
                        .when()
                        .put("/booking/" + id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Booking.class);

        assertThat(response.getFirstname()).isEqualTo("Jane");
        assertThat(response.getTotalprice()).isEqualTo(180);
    }

    @Test
    void shouldDeleteBooking() {

        int id = createBooking();
        String token = ApiHelper.getToken();

        int statusCode =
                given()
                        .spec(ApiHelper.authorizedRequestSpec(token))
                        .when()
                        .delete("/booking/" + id)
                        .then()
                        .extract()
                        .statusCode();

        assertThat(statusCode).isEqualTo(201);
    }

    // SEARCH

    @Test
    void shouldGetAllBookings() {

        var response =
                given()
                        .spec(ApiHelper.requestSpec())
                        .when()
                        .get("/booking")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("", BookingId.class);

        assertThat(response).isNotNull();
    }

    @ParameterizedTest(name = "Filter with parametrizes: {0}")
    @MethodSource("provideFilterParameters")
    void shouldFilterByFirstname(String testName, Map<String, String> queryParams) {

        var response =
                given()
                        .spec(ApiHelper.requestSpec())
                        .queryParams(queryParams)
                        .when()
                        .get("/booking")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("", BookingId.class);

        assertThat(response).isNotNull();
    }
    //additional method that provides data for test
    private static Stream<Arguments> provideFilterParameters() {
        return Stream.of(
                Arguments.of("Without filter", Map.of()),
                Arguments.of("Names", Map.of("firstname", "John")),
                Arguments.of("Dates checkin and checkout", Map.of(
                        "checkin", "2025-01-01",
                        "checkout", "2025-01-10"
                ))
        );
    }

    @Test
    void shouldFilterByDates() {

        var response =
                given()
                        .spec(ApiHelper.requestSpec())
                        .queryParam("checkin", "2025-01-01")
                        .queryParam("checkout", "2025-01-10")
                        .when()
                        .get("/booking")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("", BookingId.class);

        assertThat(response).isNotNull();
    }

    // NEGATIVE
    @ParameterizedTest(name = "GET /booking/{0} should return 404")
    @ValueSource(ints = {99999999, -1, 0})
    void shouldReturn404ForNonExistingBookingIds(int invalidId) {

        int statusCode =
                given()
                        .spec(ApiHelper.requestSpec())
                        .when()
                        .get("/booking/" + invalidId)
                        .then()
                        .extract()
                        .statusCode();

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    void shouldNotUpdateWithoutToken() {

        int id = createBooking();

        int statusCode =
                given()
                        .spec(ApiHelper.requestSpec())
                        .body(createTestBooking())
                        .when()
                        .put("/booking/" + id)
                        .then()
                        .extract()
                        .statusCode();

        assertThat(statusCode).isEqualTo(403);
    }

    @Test
    void shouldRejectInvalidDateFormat() {

        Booking invalid = Booking.builder()
                .firstname("John")
                .lastname("Doe")
                .totalprice(100)
                .depositpaid(true)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("invalid-date")
                                .checkout("invalid-date")
                                .build()
                )
                .build();

        int statusCode =
                given()
                        .spec(ApiHelper.requestSpec())
                        .body(invalid)
                        .when()
                        .post("/booking")
                        .then()
                        .extract()
                        .statusCode();

        assertThat(statusCode).isEqualTo(200);
    }

    // VALIDATION

    @Test
    void shouldMatchBookingSchema() {

        int id = createBooking();

        given()
                .spec(ApiHelper.requestSpec())
                .when()
                .get("/booking/" + id)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/booking-schema.json"
                ));
    }

    @Test
    void shouldValidateRequiredFields() {

        int id = createBooking();

        Booking response =
                given()
                        .spec(ApiHelper.requestSpec())
                        .when()
                        .get("/booking/" + id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Booking.class);

        assertThat(response.getFirstname()).isNotBlank();
        assertThat(response.getLastname()).isNotBlank();
        assertThat(response.getTotalprice()).isNotNull();
        assertThat(response.isDepositpaid()).isNotNull();

        assertThat(response.getBookingdates())
                .extracting(
                        BookingDates::getCheckin,
                        BookingDates::getCheckout
                )
                .doesNotContainNull();
    }

    @Test
    void shouldValidateDateFormat() {

        int id = createBooking();

        Booking response =
                given()
                        .spec(ApiHelper.requestSpec())
                        .when()
                        .get("/booking/" + id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Booking.class);

        assertThat(response.getBookingdates().getCheckin())
                .matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(response.getBookingdates().getCheckout())
                .matches("\\d{4}-\\d{2}-\\d{2}");
    }
}