import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoginCourierTest {
    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        String json = "{\"login\": \"ksme\", \"password\": \"1104\", \"firstName\": \"gleb\"}";
        sendPostRequestToCreateCourier(json);
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    public void theCourierCanLogInTest() {
        Response response = sendPostRequestToLoginCourier("{\"login\": \"ksme\", \"password\": \"1104\"}");
        compareStatusCode(response, 200);
    }

    @Test
    @DisplayName("Ошибка авторизации при неверном логине")
    public void authorizationErrorWithInvalidLoginTest() {
        Response response = sendPostRequestToLoginCourier("{\"login\": \"ksme11\", \"password\": \"1104\"}");
        compareStatusCode(response, 404);
    }

    @Test
    @DisplayName("Ошибка авторизации при неверном пароле")
    public void authorizationErrorWithInvalidPasswordTest() {
        Response response = sendPostRequestToLoginCourier("{\"login\": \"ksme\", \"password\": \"1234\"}");
        compareStatusCode(response, 404);
    }

    @Test
    @DisplayName("Ошибка 400 если в теле запроса отсутствует обязательное поле")
    public void errorIfOneRequiredFieldIsMissingInTheRequestBodyTest() {
        Response response = sendPostRequestToLoginCourier("{\"login\": \"ksme\"");
        compareStatusCode(response, 400);
    }

    @Test
    @DisplayName("Успешный запрос возвращает id курьера")
    public void aSuccessfulRequestReturnsTheCourierIDTest() {
        Response response = sendPostRequestToLoginCourier("{\"login\": \"ksme\", \"password\": \"1104\"}");
        compareStatusCodeAndResponseBody(response, 200, "id");
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier")
    public static Response sendPostRequestToCreateCourier(String json) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier/login")
    public Response sendPostRequestToLoginCourier(String json) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Проверка статус-кода ответа")
    public void compareStatusCode(Response response, int code) {
        response.then()
                .statusCode(code);
    }

    @Step("Проверка статус-кода и тела ответа")
    public void compareStatusCodeAndResponseBody(Response response, int code, String key) {
        response.then()
                .statusCode(code)
                .assertThat()
                .body(key, Matchers.notNullValue());
    }

    @AfterClass
    public static void tearDown() {
        Response response = sendPostRequestToLoginCourierAfterTest("{\"login\": \"ksme\", \"password\": \"1104\"}");
        deleteCourier(response);
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier/login после тестов")
    public static Response sendPostRequestToLoginCourierAfterTest(String json) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера")
    public static void deleteCourier(Response response) {
        if (response.getStatusCode() == 200) {
            int id = response.path("id");
            RestAssured.delete("/api/v1/courier/" + String.valueOf(id));
        }
    }
}
