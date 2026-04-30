import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.qa_scooter.praktikum_services.Order;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }
    private List<String> color;
    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")}
        };
    }

    private Order order = new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha");

    @Test
    @DisplayName("Успешное создание заказа с выбранным цветом")
    public void creatingAnOrderWithColorsTest() {
        order.setColor(color);
        Response response = sendPostRequestToCreateOrder(order);
        compare(response);
        cancelOrder(response);
    }

    @Step("Отправка POST запроса на ручку /api/v1/orders")
    public Response sendPostRequestToCreateOrder(Order order) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Сравнение ОР с ФР")
    public void compare(Response response) {
        response.then()
                .assertThat().body("track", Matchers.notNullValue())
                .and()
                .statusCode(201);
    }

    @Step("Отправка PUT запроса на ручку /api/v1/orders/cancel")
    public void cancelOrder(Response response) {
        if (response.getStatusCode() == 201) {
            RestAssured.given()
                    .header("Content-type", "application/json")
                    .and()
                    .body("{\"track\": \"" + String.valueOf(response.path("track") + "\"}"))
                    .when()
                    .put("/api/v1/orders/cancel");
        }
    }
}
