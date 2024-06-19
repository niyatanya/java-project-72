package hexlet.code.dto;

import hexlet.code.model.Url;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class UrlsPage extends BasePage {
    private List<Url> urls;
}
