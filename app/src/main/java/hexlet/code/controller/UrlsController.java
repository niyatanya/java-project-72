package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import static io.javalin.rendering.template.TemplateUtil.model;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.net.URI;
import java.net.URL;

public class UrlsController {
    public static void build(Context ctx) throws SQLException {
        String urlString = ctx.formParam("url").toLowerCase().trim();

        // Если введен корректный URL, нормализуем его
        String normalizedUrlString = null;
        try {
            URI inputUri = new URI(urlString);
            System.out.println("--> Успешная конвертация введенного урла в объект URI: " + inputUri);
            URL inputUrl = inputUri.toURL();
            System.out.println("--> Успешная конвертация объекта URI в объект URL: " + inputUrl);
            normalizedUrlString = String.format("%s://%s%s",
                    inputUrl.getProtocol(),
                    inputUrl.getHost(),
                    (inputUrl.getPort() == -1 ? "" : ":" + inputUrl.getPort()));
            System.out.println("--> Нормализованный урл, проверенный программой и направляемый на сохранение: "
                    + normalizedUrlString);

            // Если такого url еще нет, добавляем его в базу
            if (!UrlRepository.urlExists(normalizedUrlString)) {
                System.out.println("--> НАЧАЛО ВЫПОЛНЕНИЯ ПРОГРАММЫ");
                System.out.println("--> Проверка наличия урл в базе. Урл в базе не обнаружен.");
                Url url = new Url(normalizedUrlString);
                UrlRepository.save(url);
                List<Url> urls = UrlRepository.getEntities();
                System.out.println("--> Распечатываю все урлы из базы: " + urls.toString());
                System.out.println("--> Кол-во строк в базе: " + urls.size());
                UrlsPage page = new UrlsPage(urls);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("alertType", "success");
                page.setFlash(ctx.consumeSessionAttribute("flash"));
                page.setAlertType(ctx.consumeSessionAttribute("alertType"));
                ctx.render("urls/index.jte", model("page", page));
            } else {
                List<Url> urls = UrlRepository.getEntities();
                UrlsPage page = new UrlsPage(urls);
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("alertType", "danger");
                page.setFlash(ctx.consumeSessionAttribute("flash"));
                page.setAlertType(ctx.consumeSessionAttribute("alertType"));
                ctx.render("urls/index.jte", model("page", page));
            }
            System.out.println("--> КОНЕЦ ВЫПОЛНЕНИЯ ПРОГРАММЫ");
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            System.out.println("--> Распечатываю исключение: " + e);
            BasePage page = new BasePage();
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("alertType", "danger");
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setAlertType(ctx.consumeSessionAttribute("alertType"));
            ctx.render("index.jte", model("page", page));
        }
    }

    public static void show(Context ctx) throws SQLException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Page not found"));
        UrlPage page = new UrlPage(url);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        UrlsPage page = new UrlsPage(urls);
        ctx.render("urls/index.jte", model("page", page));
    }
}
