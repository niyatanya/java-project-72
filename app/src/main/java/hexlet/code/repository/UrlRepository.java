package hexlet.code.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import hexlet.code.model.Url;

public class UrlRepository extends BaseRepository {

    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT * FROM urls";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();

            List<Url> result = new ArrayList<>();
            while (resultSet.next()) {
                System.out.println("--> НАЧАЛО ПОЛУЧЕНИЯ ДАННЫХ ИЗ БАЗЫ");
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                Url url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                System.out.println("--> Распечатываю сохраняемый урл: " + url.toString());
                result.add(url);
            }
            System.out.println("--> КОНЕЦ ПРОЦЕДУРЫ ПОЛУЧЕНИЯ ДАННЫХ ИЗ БД. Распечатываю список урл: "
                    + result.toString());
            return result;
        }
    }

    public static Boolean urlExists(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    public static void save(Url url) throws SQLException {
        System.out.println("--> НАЧАЛО ПРОЦЕДУРЫ ДОБАВЛЕНИЯ");
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(2, createdAt);
            url.setCreatedAt(createdAt);
            System.out.println("--> Распечатываю sql-запрос: " + stmt);
            stmt.executeUpdate();

            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println("--> ЧТЕНИЕ ОТВЕТА БАЗЫ");
                url.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
            System.out.println("--> Распечатываю новый url: " + url);
            System.out.println("--> Проверяю наличие нового url в базе: " + find(url.getId()).orElseThrow());
            System.out.println("--> Распечатываю всю базу: " + getEntities());
            System.out.println("--> КОНЕЦ ПРОЦЕДУРЫ ДОБАВЛЕНИЯ");
        }
    }

    public static Optional<Url> find(int id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                Url url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                return Optional.of(url);
            }
        }
        return Optional.empty();
    }
}
