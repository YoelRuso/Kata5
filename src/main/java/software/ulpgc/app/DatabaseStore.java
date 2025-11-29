package software.ulpgc.app;

import software.ulpgc.architecture.io.Store;
import software.ulpgc.architecture.model.Game;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

public class DatabaseStore implements Store {
    private final Connection connection;

    public DatabaseStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Stream<Game> games() {
        try {
            return gamesIn(resultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSet resultSet() throws SQLException {
        return connection.createStatement().executeQuery("SELECT * FROM games");
    }

    public Stream<Game> gamesIn(ResultSet rs) {
        return Stream.generate(()-> nextGameIn(rs))
                .onClose(()->close(rs))
                .takeWhile(Objects::nonNull);
    }

    private void close(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Game nextGameIn(ResultSet rs) {
        try {
            return rs.next() ? readGameIn(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Game readGameIn(ResultSet rs) throws SQLException {
        return new Game(
                rs.getString(1),
                rs.getString(2),
                rs.getInt(3)
        );
    }

    public Connection connection() {
        return connection;
    }
}
