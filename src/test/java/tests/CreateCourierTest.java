package tests;

import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.qa_scooter.praktikum_services.Courier;

public class CreateCourierTest {
    private CourierApi courierApi;
    private String login = "ksme";
    private String password = "1104";
    private String firstName = "gleb";

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        courierApi = new CourierApi();
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьера можно создать с валидным телом запроса и ответ содержит ok=true.")
    public void aCourierCanBeCreatedTest() {
        Courier courier = new Courier(login, password, firstName);
        Response response = courierApi.sendPostRequestToCreateCourier(courier);
        courierApi.compareStatusCode(response, HttpStatus.SC_CREATED);
    }

    @Test
    @DisplayName("Ошибка 409 если создать двух одинаковых курьеров")
    @Description("Проверка, что создание дубликата курьера возвращает статус-код 409 и сообщение об ошибке.")
    public void aCourierCannotBeCreatedTwiceTest() {
        Courier courier = new Courier(firstName, password, firstName);
        courierApi.sendPostRequestToCreateCourier(courier);
        Response response = courierApi.sendPostRequestToCreateCourier(courier);
        courierApi.compareResponseBody(response, "message", "Этот логин уже используется");
        courierApi.compareStatusCode(response, HttpStatus.SC_CONFLICT);
    }

    @Test
    @DisplayName("Успешное создание курьера если ввести значения в ручку")
    @Description("Проверка, что курьера можно создать, если обязательные значения переданы как query-параметры.")
    public void theCourierWillBeCreatedIfEnterTheValuesInTheURLTest() {
        Response response = courierApi.sendPostRequestToCreateCourierWithQueryParams();
        courierApi.compareStatusCode(response, HttpStatus.SC_CREATED);
    }

    @Test
    @DisplayName("Правильное сообщение в теле ответа если ввести валидные данные в теле запроса")
    @Description("Проверка, что успешный запрос на создание курьера возвращает ожидаемое тело ответа.")
    public void aSuccessfulRequestReturnsTheCorrectResponseBodyTest() {
        Courier courier = new Courier(login, password, firstName);
        Response response = courierApi.sendPostRequestToCreateCourier(courier);
        courierApi.compareResponseBody(response, "ok", true);
    }

    @Test
    @DisplayName("Ошибка 400 если отправить запрос без логина")
    @Description("Проверка, что создание курьера без одного обязательного поля возвращает статус-код 400.")
    public void errorIfLoginFieldIsMissingFromTheRequestBodyTest() {
        Courier courier = new Courier();
        courier.setPassword(password);
        courier.setFirstName(firstName);
        Response response = courierApi.sendPostRequestToCreateCourier(courier);
        courierApi.compareResponseBody(response, "message", "Недостаточно данных для создания учетной записи");
        courierApi.compareStatusCode(response, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Ошибка 400 если отправить запрос без пароля")
    @Description("Проверка, что создание курьера без одного обязательного поля возвращает статус-код 400.")
    public void errorIfPasswordFieldIsMissingFromTheRequestBodyTest() {
        Courier courier = new Courier();
        courier.setLogin(login);
        courier.setFirstName(firstName);
        Response response = courierApi.sendPostRequestToCreateCourier(courier);
        courierApi.compareResponseBody(response, "message", "Недостаточно данных для создания учетной записи");
        courierApi.compareStatusCode(response, HttpStatus.SC_BAD_REQUEST);
    }

    @After
    public void tearDown() {
        Courier courier = new Courier();
        courier.setLogin(login);
        courier.setPassword(password);
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.deleteCourier(response);
    }
}
