package tests;

import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ListOrdersTest {
    private OrderApi orderApi;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        orderApi = new OrderApi();
    }

    @Test
    @DisplayName("Тело ответа возвращает список заказов")
    @Description("Проверка, что ручка получения списка заказов возвращает коллекцию orders в теле ответа.")
    public void theResponseBodyReturnsAListOfOrders() {
        Response response = orderApi.sendGetRequestToGetOrders();
        orderApi.compareResponseBody(response);
    }


}
