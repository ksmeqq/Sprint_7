package api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import ru.qa_scooter.praktikum_services.Courier;

public class CourierApi {

    @Step("Отправка POST запроса на ручку /api/v1/courier с телом запроса")
    public Response sendPostRequestToCreateCourier(Courier courier) {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier с query-параметрами")
    public Response sendPostRequestToCreateCourierWithQueryParams() {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .queryParam("login", "ksme")
                .queryParam("password", "1104")
                .queryParam("firstName", "gleb")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Отправка POST запроса на ручку /api/v1/courier/login")
    public Response sendPostRequestToLoginCourier(Courier courier) {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера")
    public void deleteCourier(Response loginResponse) {
        if (loginResponse.getStatusCode() == 200) {
            int id = loginResponse.path("id");
            RestAssured.given()
                    .spec(ApiSpec.getRequestSpec())
                    .when()
                    .delete("/api/v1/courier/" + id);
        }
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

    @Step("Проверка тела ответа")
    public void compareResponseBody(Response response, String key, Object expectedValue) {
        Object actualValue = response.path(key);
        Assert.assertEquals(expectedValue, actualValue);
    }
}
