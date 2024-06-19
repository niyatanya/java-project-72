package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.javalin.testtools.JavalinTest;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    Javalin app;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
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
    public void testUrlPage() throws SQLException {
        Url url = new Url("https://www.example2.com");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }
}
