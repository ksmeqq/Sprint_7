package tests;

import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.qa_scooter.praktikum_services.Courier;

public class LoginCourierTest {
    private static CourierApi courierApi;
    private static String login = "ksme";
    private static String password = "1104";
    private static String firstName = "gleb";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        courierApi = new CourierApi();
        Courier courier = new Courier(login, password, firstName);
        courierApi.sendPostRequestToCreateCourier(courier);
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с валидными учетными данными.")
    public void theCourierCanLogInTest() {
        Courier courier = new Courier();
        courier.setLogin(login);
        courier.setPassword(password);
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.compareStatusCode(response, HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Ошибка авторизации при неверном логине")
    @Description("Проверка, что авторизация с неверным логином возвращает статус-код 404.")
    public void authorizationErrorWithInvalidLoginTest() {
        Courier courier = new Courier();
        courier.setLogin("ksme11");
        courier.setPassword(password);
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.compareStatusCode(response, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Ошибка авторизации при неверном пароле")
    @Description("Проверка, что авторизация с неверным паролем возвращает статус-код 404.")
    public void authorizationErrorWithInvalidPasswordTest() {
        Courier courier = new Courier();
        courier.setLogin(login);
        courier.setPassword("1234");
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.compareStatusCode(response, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Ошибка 400 если в теле запроса отсутствует пароль")
    @Description("Проверка, что авторизация без обязательного поля возвращает статус-код 400.")
    public void errorIfPasswordFieldIsMissingInTheRequestBodyTest() {
        Courier courier = new Courier();
        courier.setLogin(login);
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.compareStatusCode(response, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Ошибка 400 если в теле запроса отсутствует логин")
    @Description("Проверка, что авторизация без обязательного поля возвращает статус-код 400.")
    public void errorIfLoginFieldIsMissingInTheRequestBodyTest() {
        Courier courier = new Courier();
        courier.setPassword(password);
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.compareStatusCode(response, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Успешный запрос возвращает id курьера")
    @Description("Проверка, что успешный ответ авторизации содержит id курьера.")
    public void aSuccessfulRequestReturnsTheCourierIDTest() {
        Courier courier = new Courier();
        courier.setLogin(login);
        courier.setPassword(password);
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.compareStatusCodeAndResponseBody(response, HttpStatus.SC_OK, "id");
    }

    @AfterClass
    public static void tearDown() {
        Courier courier = new Courier();
        courier.setLogin(login);
        courier.setPassword(password);
        Response response = courierApi.sendPostRequestToLoginCourier(courier);
        courierApi.deleteCourier(response);
    }
}
