import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ListOrdersTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Тело ответа возвращает список заказов")
    public void theResponseBodyReturnsAListOfOrders() {
        Response response = sendGetRequestToGetOrders();
        compareResponseBody(response);
    }

    @Step("Отправка GET запроса на ручку /api/v1/orders")
    public Response sendGetRequestToGetOrders() {
        return RestAssured.given()
                .when()
                .get("/api/v1/orders");
    }

    @Step("Проверка тела ответа")
    public void compareResponseBody(Response response) {
        response.then()
                .assertThat()
                .body("orders", Matchers.instanceOf(List.class));
    }
}
