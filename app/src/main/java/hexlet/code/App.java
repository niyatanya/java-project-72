package hexlet.code;

import hexlet.code.repository.BaseRepository;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlsController;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.Utils;

import io.javalin.Javalin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class App {
    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        app.start(Utils.getPort());
    }

    public static Javalin getApp() throws SQLException, IOException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(Utils.getDataBaseUrl());
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        String sql = Utils.readResourceFile("schema.sql");

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(Utils.createTemplateEngine()));
        });

        app.get(NamedRoutes.rootPath(), RootController::index);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.checkUrlPath("{id}"), UrlsController::createCheck);

        return app;
    }
}
