package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlChecksRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, urlCheck.getUrlId());
            stmt.setInt(2, urlCheck.getStatusCode());
            stmt.setString(3, urlCheck.getTitle());
            stmt.setString(4, urlCheck.getH1());
            stmt.setString(5, urlCheck.getDescription());

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            urlCheck.setCreatedAt(createdAt);
            stmt.setTimestamp(6, createdAt);
            stmt.executeUpdate();

            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> getEntities(int urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, urlId);
            var resultSet = stmt.executeQuery();

            List<UrlCheck> result = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int statusCode = resultSet.getInt("status_code");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");

                UrlCheck urlCheck = new UrlCheck(id, statusCode, title, h1, description, urlId, createdAt);
                result.add(urlCheck);
            }
            return result;
        }
    }

    public static Optional<UrlCheck> find(int id) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int urlId = resultSet.getInt("url_id");
                int statusCode = resultSet.getInt("status_code");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");

                UrlCheck urlCheck = new UrlCheck(id, statusCode, title, h1, description, urlId, createdAt);
                return Optional.of(urlCheck);
            }
        }
        return Optional.empty();
    }
}
