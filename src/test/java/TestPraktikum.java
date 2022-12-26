// импортируем RestAssured

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
// импортируем Before
import io.restassured.response.Response;
import org.junit.Before;
// импортируем Test
import org.junit.Test;
// дополнительный статический импорт нужен, чтобы использовать given(), get() и then()
import java.io.File;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestPraktikum {
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MzU0MmJjZjM2OTc4NDAwM2QwZTcwN2QiLCJpYXQiOjE2Njk3MDY4MjYsImV4cCI6MTY3MDMxMTYyNn0.J94-7iJg5LSTcWyC6i3Km3YJ6QuW6hhwPE62MJlO-TI";

    // аннотация Before показывает, что метод будет выполняться перед каждым тестовым методом
    @Before
    public void setUp() {
        // повторяющуюся для разных ручек часть URL лучше записать в переменную в методе Before
        // если в классе будет несколько тестов, указывать её придётся только один раз
        RestAssured.baseURI = "https://qa-mesto.praktikum-services.ru";
    }

    /* ------  GET   -------*/
    // создаём метод автотеста
    @Test
    @DisplayName("Check GET user info")
    @Description("Basic test for /users/me endpoint")
    public void getMyInfoStatusCode() {
        // метод given() помогает сформировать запрос
        given()
                // указываем протокол и данные авторизации
                .auth().oauth2(token)
                // отправляем GET-запрос с помощью метода get, недостающую часть URL (ручку) передаём в него в качестве параметра
                .get("/api/users/me")
                // проверяем, что статус-код ответа равен 200
                .then().statusCode(200);
    }

    // проверка имени пользователя
    @Test
    @DisplayName("Check user name")
    @Description("Basic test for /users/me endpoint")
    public void checkUserName() {
        given()
                .auth().oauth2(token)
                .get("/api/users/me")
                .then().assertThat().body("data.name", equalTo("Жак-Ив Кусто"));
    }

    // API карточек отдаёт 200 ок
    @Test
    @DisplayName("Check GET /api/cards - 200 OK")
    public void checkCardsStatusCode200() {
        // проверяем статус-код ответа на запрос «Получение всех карточек»
        given()
                .auth().oauth2(token)
                .get("/api/cards")
                .then().statusCode(200);
    }

    // правильно ли указано занятие пользователя
    @Test
    @DisplayName("Check about user")
    public void checkUserActivityAndPrintResponseBody() {

        // отправляет запрос и сохраняет ответ в переменную response, экзмепляр класса Response
        Response response = given()
                .auth().oauth2(token)
                .get("/api/users/me");
        // проверяет, что в теле ответа ключу about соответствует нужное занятие
        response.then().assertThat().body("data.about", equalTo("Тестировщик"));
        // выводит тело ответа на экран
        System.out.println(response.asString());
    }

    /* ---------- POST  PATCH ----- */
    // создание новой карточки места
    @Test
    public void createNewPlaceAndCheckResponse() {
        File json = new File("src/test/resources/newCard.json");
//        или строкой
//        String json = "{\"name\": \"Очень интересное место\", \"link\": \"https://code.s3.yandex.net/qa-automation-engineer/java/files/paid-track/sprint1/photoSelenide.jpg\"}";;

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .and()
                        .body(json)
                        .when()
                        .post("/api/cards");
        response.then().assertThat().body("data._id", notNullValue())
                .and()
                .statusCode(201);
    }

    //    тест, который отправляет PATCH-запрос на ручку /api/users/me и проверяет, что:
//    Статус ответа — 200.
//    Значения поля name такое же, как имя в JSON-файле.
    @Test
    public void updateProfileAndCheckStatusCode() {
        File json = new File("src/test/resources/updateProfile.json"); // запиши файл в файловую переменную
        Response response =
                given()
                        .header("Content-type", "application/json") // заполни header
                        .auth().oauth2(token)
                        .and()
                        .body(json) // заполни body
                        .when()
                        .patch("/api/users/me"); // отправь запрос на ручку
        response.then().assertThat()
                .statusCode(200) // проверь статус ответа
                .body("data.name", equalTo("Василий Васильев")); // проверь поле name

        // вернули имя и профессию как было, чтобы не ломать следующие тесты
        SerializedCardForJSON serializedCardForJSON = new SerializedCardForJSON("Жак-Ив Кусто", "Тестировщик"); // запиши файл в файловую переменную
                given()
                        .header("Content-type", "application/json") // заполни header
                        .auth().oauth2(token)
                        .and()
                        .body(serializedCardForJSON) // заполни body
                        .when()
                        .patch("/api/users/me") // отправь запрос на ручку
                        .then().assertThat().statusCode(200); // проверь статус ответа
    }

    // body через Сериализацию (Класс CardForJSON)
    @Test
    public void updateProfileAndCheckStatusCodeFromClassCardForJSON() {
        SerializedCardForJSON serializedCardForJSON = new SerializedCardForJSON("Жак-Ив Кусто", "Тестировщик"); // запиши файл в файловую переменную
        Response response =
                given()
                        .header("Content-type", "application/json") // заполни header
                        .auth().oauth2(token)
                        .and()
                        .body(serializedCardForJSON) // заполни body
                        .when()
                        .patch("/api/users/me"); // отправь запрос на ручку
        response.then().assertThat()
                .statusCode(200) // проверь статус ответа
                .body("data.name", equalTo("Жак-Ив Кусто")) // проверь поле name
                .body("data.about", equalTo("Тестировщик"));
    }

    // Десериализация ответа в класс
    @Test
    public void CheckUser() {
        User user = given()
                .auth().oauth2(token)
                .get("/api/users/me")
// попросили представить результат как объект типа DeserializedResponse
                .as(User.class);
        Data data = user.getData();
        assertThat(user, is(notNullValue()));
        assertThat("Жак-Ив Кусто", equalTo(data.getName()));
        assertThat("Тестировщик", equalTo(data.getAbout()));
    }


    /*
    Данные можно описать в виде моделей — Java-классов.
    Чтобы добавить данные, нужно создать экземпляры этих классов и заполнить их поля.
    Тебе уже удалось проделать похожее в уроке про сериализацию. Такой класс называется POJO.
    Тестовые данные — экземпляр класса Card — можно передать в запрос:
     */
    @Test
    public void createNewCard2() {
        for (int i = 0; i < 10; i++) {
            Card card = new Card(String.format("%s-%d", "Москва", i),
                    "https://code.s3.yandex.net/qa-automation-engineer/java/files/paid-track/sprint1/photoSelenium.jpg"); // экземпляр класса Card со значениями полей

            given()
                    .header("Content-type", "application/json") // передача Content-type в заголовке для указания типа файла
                    .auth().oauth2(token) // передача токена для аутентификации
                    .and()
                    .body(card) // передача объекта с данными
                    .when()
                    .post("/api/cards") // отправка POST-запроса
                    .then().statusCode(201); // проверка кода ответа
        }
    }

    /*
    Запрос данных у приложения
Например, ты тестируешь метод, который ставит лайк на фотографию в Mesto.
Чтобы не добавлять фотографии для проверки самостоятельно, можно получить список фотографий от приложения.
Для этого нужно:
1. Вызвать метод, который возвращает фотографии.
2. Сохранить параметр _id первой фотографии из списка.
3. Вызвать метод, который ставит лайк, и передать параметр.
4. Вызвать метод, который удаляет лайк, и передать параметр.
     */
    @Test
    public void likeTheFirstPhoto() {
        String oauthToken = token;

        // получение списка фотографий и сохранение _id первой фотографии
        String photoId = given()
                .auth().oauth2(oauthToken) // аутентификация при выполнении запроса
                .get("/api/cards") // отправка GET-запроса
                .then().extract().body().path("data[0]._id"); // получение ID фотографии из массива данных

        // лайк первой фотографии
        given()
                .auth().oauth2(oauthToken) // аутентификация при выполнении запроса
                .put("/api/cards/{photoId}/likes", photoId) // отправка PUT-запроса
                .then().assertThat().statusCode(200); // проверка, что сервер вернул код 200

        // снять лайк с первой фотографии
        given()
                .auth().oauth2(oauthToken) // аутентификация при выполнении запроса
                .delete("/api/cards/{photoId}/likes", photoId) // отправка DELETE-запроса
                .then().assertThat().statusCode(200); // проверка, что сервер вернул код 200
    }


    // ДОБАВИТЬ ВЫТАСКИВАНИЕ ТОКЕНА ИЗ ЗАПРОСА!!!!
    @Test
    @Description ("Check auth sirigij514@24rumen.com")
    public void checkAuth() {
                String json = "{\"email\": \"sirigij514@24rumen.com\", \"password\": \"Qwerty123\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/signin")
                .then().statusCode(200);
    }
}