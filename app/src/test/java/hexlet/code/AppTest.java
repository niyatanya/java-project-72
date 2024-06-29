package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import java.util.List;
import io.javalin.Javalin;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.javalin.testtools.JavalinTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;
    private static MockWebServer mockWebServer;

    public static final String TEST_HTML_PAGE = "TestHTMLPage.html";

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws Exception {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeAll
    static void generalSetUp() throws Exception {
        mockWebServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse()
                .setBody(readFixture(TEST_HTML_PAGE))
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.start();
    }

    @BeforeEach
    public final void setUpForEachTest() throws IOException, SQLException {
        app = App.getApp();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
        app.stop();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example1.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example1.com");
        });
    }

    @Test
    public void testCreateInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=hhhh3333";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).doesNotContain("hhhh3333");
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        Url url = new Url("https://www.example2.com");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example2.com");
        });
    }

    @Test
    public void testCheckUrl() throws SQLException {
        // Описание работы теста:
        // 1. В @BeforeAll создаю мок-сервер и передаю туда один мок-ответ (mockResponse).
        // Ответ состоит из кода состояния 200 и body - HTML-страницы, которая сохранена в фикстурах.
        // 2. Тест вместо обращения к реальной странице должен достать мок-ответ из мок-сервера.
        // 3. Для этого создается мок-URL mockUrlName. На основе него создается инстанс Url.
        // С этим инстансом в реальную программу передается mockUrlName, при обращении к которому запрос должен
        // уходить на mockWebServer и получить оттуда мок-респонс (mockResponse).

        // Внутри теста создаем мок-УРЛ, запускаем его проверку программой и считываем сохраненный мок-результат
        JavalinTest.test(app, (server, client) -> {
            String mockUrlName = mockWebServer.url("/").toString();
            Url mockUrl = new Url(mockUrlName);
            UrlRepository.save(mockUrl);
            var response = client.post(NamedRoutes.checkUrlPath(String.valueOf(mockUrl.getId())));

            // Проверка на положительный ответ сервера по адресу NamedRoutes.checkUrlPath(id)
            assertThat(response.code()).isEqualTo(200);

            // Сохраняем бади ответа в переменную. Важно это сделать один раз, а не каждый раз для отдельного ассерта
            String responseBody = response.body().string();

            // Проверяем, что вернувшееся от сервера бади содержит данные из фикстуры
            assertThat(responseBody).contains("HTML test page");
            assertThat(responseBody).contains("The best test page for all possible scenarios");
            assertThat(responseBody).contains("Discover this test HTML page tailored for web applications"
               + " testing. Featuring headers, paragraphs, title and meta data, ideal for evaluating functionality.");

            List<UrlCheck> urlChecks = UrlChecksRepository.getEntities(mockUrl.getId());

            // Проверяем, что в таблице с проверками появилась одна запись
            assertThat(urlChecks.size()).isEqualTo(1);

            // Проверяем, что сохраненная проверка имеет ожидаемые айди, код ответа и дату
            UrlCheck lastUrlCheck = UrlChecksRepository.getEntities(mockUrl.getId()).getFirst();
            int urlId = lastUrlCheck.getUrlId();
            int statusCode = lastUrlCheck.getStatusCode();
            assertThat(urlId).isEqualTo(1);
            assertThat(statusCode).isEqualTo(200);
            assertThat(lastUrlCheck.getCreatedAt()).isToday();

            // Проверяем, что сохраненная проверка имеет ожидаемые данные из TEST_HTML_PAGE
            String title = lastUrlCheck.getTitle();
            assertThat(title).contains("HTML test page");
            String h1 = lastUrlCheck.getH1();
            assertThat(h1).contains("The best test page for all possible scenarios");
            String description = lastUrlCheck.getDescription();
            assertThat(description).contains("Discover this test HTML page tailored for web applications"
                 + " testing. Featuring headers, paragraphs, title and meta data, ideal for evaluating functionality.");
        });
    }
}
