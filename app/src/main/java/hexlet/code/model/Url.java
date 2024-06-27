package hexlet.code.model;

import java.sql.Timestamp;
import java.util.List;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Url {
    private int id;
    private String name;
    private Timestamp createdAt;
    private List<UrlCheck> urlChecks;

    public Url(String name) {
        this.name = name;
    }
}
