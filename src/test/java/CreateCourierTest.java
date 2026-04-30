import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateCourierTest {
    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Успешное создание курьера")
    public void aCourierCanBeCreatedTest() {
        Response response = sendPostRequestToCreateCourier();
        compare(response, 201, "ok", "true");
    }

    @Test
    @DisplayName("Ошибка 409 если создать двух одинаковых курьеров")
    public void aCourierCannotBeCreatedTwiceTest() {
        Response response = sendPostRequestToCreateCourier();
        response = sendPostRequestToCreateCourier();
        compare(response, 409, "message", "Этот логин уже используется");
    }

    @Test
    @DisplayName("Успешное создание курьера если ввести значения в ручку")
    public void theCourierWillBeCreatedIfEnterTheValuesInTheURLTest() {
        Response response = sendPostRequestToCreateCourierWithQueryParams();
        compareStatusCode(response, 201);
    }

    @Test
    @DisplayName("Успешное создание курьера и правильное сообщение в теле ответа если ввести валидные данные в теле запроса")
    public void aSuccessfulRequestReturnsTheCorrectResponseBodyTest() {
        String json = "{\"login\": \"ksme\", \"password\": \"1104\", \"firstName\": \"gleb\"}";
        Response response = sendPostRequestToCreateCourier(json);
        compareResponseBody(response, "ok", true);
    }

    @Test
    @DisplayName("Ошибка 400 если отправить запрос без одного из обязательных полей")
    public void errorIfOneOfTheRequiredFieldsIsMissingFromTheRequestBodyTest() {
        String json = "{\"login\": \"ksme\"}";
        Response response = sendPostRequestToCreateCourier(json);
        compareStatusCode(response, 400);
    }

    @Test
    @DisplayName("Ошибка 409 если создать двух курьеров с одинаковыми логинами")
    public void errorWhenCreatingTwoCouriersWithTheSameLoginTest() {
        String json = "{\"login\": \"ksme\", \"password\": \"1104\", \"firstName\": \"gleb\"}";
        sendPostRequestToCreateCourier(json);
        Response response = sendPostRequestToCreateCourier("{\"login\": \"ksme\", \"password\": \"2006\", \"firstName\": \"gleb\"}");
        compareStatusCode(response, 409);
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier")
    public Response sendPostRequestToCreateCourier() {
        String json = "{\"login\": \"ksme\", \"password\": \"1104\", \"firstName\": \"gleb\"}";
        return sendPostRequestToCreateCourier(json);
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier с телом запроса")
    public Response sendPostRequestToCreateCourier(String json) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier с query параметрами")
    public Response sendPostRequestToCreateCourierWithQueryParams() {
        return RestAssured.given()
                .queryParam("login", "ksme")
                .queryParam("password", "1104")
                .queryParam("firstName", "gleb")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Сравнение ОР с ФР")
    public void compare(Response response, int code, String key, String value) {
        if (code == 201) {
            response.then()
                    .assertThat()
                    .body("ok", Matchers.equalTo(true))
                    .and()
                    .statusCode(code);
        } else {
            response.then()
                    .assertThat()
                    .body(key, Matchers.equalTo(value))
                    .and()
                    .statusCode(code);
        }
    }

    @Step("Проверка статус-кода ответа")
    public void compareStatusCode(Response response, int code) {
        response.then()
                .statusCode(code);
    }

    @Step("Проверка тела ответа")
    public void compareResponseBody(Response response, String key, Object value) {
        response.then()
                .assertThat()
                .body(key, Matchers.equalTo(value));
    }

    @After
    public void tearDown() {
        Response response = RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"login\": \"ksme\", \"password\": \"1104\"}")
                .when()
                .post("/api/v1/courier/login");
        if (response.getStatusCode() == 200) {
            int id = response.path("id");
            RestAssured.delete("/api/v1/courier/" + String.valueOf(id));
        }
    }
}
