package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UrlPage extends BasePage {
    private Url url;
}
