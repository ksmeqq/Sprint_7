package tests;

import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.qa_scooter.praktikum_services.Order;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final List<String> color;
    private OrderApi orderApi;
    private Order order = new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", Arrays.asList(""));
    private int track;

    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    @Parameterized.Parameters(name = "Тестовые цвета: {0}")
    public static Object[][] data() {
        return new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")}
        };
    }

    @Test
    @DisplayName("Успешное создание заказа с выбранным цветом")
    @Description("Проверка, что заказ можно создать с каждым параметризованным вариантом цвета самоката и ответ содержит track.")
    public void creatingAnOrderWithColorsTest() {
        order.setColor(color);
        Response response = orderApi.sendPostRequestToCreateOrder(order);
        track = response.path("track");
        orderApi.compare(response);
    }

    @After
    public void tearDown() {
        orderApi.cancelOrder(track);
    }
}
