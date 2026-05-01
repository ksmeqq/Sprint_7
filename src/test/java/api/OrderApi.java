package api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import ru.qa_scooter.praktikum_services.Order;

import java.util.List;

public class OrderApi {

    @Step("Отправка POST запроса на ручку /api/v1/orders")
    public Response sendPostRequestToCreateOrder(Order order) {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Отправка GET запроса на ручку /api/v1/orders")
    public Response sendGetRequestToGetOrders() {
        return RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .when()
                .get("/api/v1/orders");
    }

    @Step("Отправка PUT запроса на ручку /api/v1/orders/cancel")
    public void cancelOrder(int track) {
        RestAssured.given()
                .spec(ApiSpec.getRequestSpec())
                .header("Content-type", "application/json")
                .and()
                .body("{\"track\": \"" + String.valueOf(track) + "\"}")
                .when()
                .put("/api/v1/orders/cancel");
    }

    @Step("Сравнение ожидаемого результата с фактическим")
    public void compare(Response response) {
        response.then()
                .assertThat().body("track", Matchers.notNullValue())
                .and()
                .statusCode(HttpStatus.SC_CREATED);
    }

    @Step("Проверка тела ответа")
    public void compareResponseBody(Response response) {
        response.then()
                .assertThat()
                .body("orders", Matchers.instanceOf(List.class));
    }
}
