package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public final class UrlsPage extends BasePage {
    private List<Url> urls;
    private List<UrlCheck> urlChecks;

    public List<UrlCheck> getUrlChecksByUrlId(int urlId) {
        return urlChecks.stream()
                .filter(c -> c.getUrlId() == urlId)
                .toList();
    }
}
