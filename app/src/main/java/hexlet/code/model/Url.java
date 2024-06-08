package hexlet.code.model;

import java.sql.Timestamp;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class Url {
    private int id;
    private String name;
    private Timestamp createdAt;

    public Url(String name, Timestamp createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }
}
