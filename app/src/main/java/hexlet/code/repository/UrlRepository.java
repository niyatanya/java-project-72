package hexlet.code.repository;

import java.sql.ResultSet;
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
                result.add(getUrlFromResultSet(resultSet));
            }
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
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            url.setCreatedAt(createdAt);
            stmt.setTimestamp(2, createdAt);
            stmt.executeUpdate();

            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Url> find(int id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getUrlFromResultSet(resultSet));
            }
        }
        return Optional.empty();
    }

    public static Url getUrlFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new Url(id, name, createdAt);
    }
}
