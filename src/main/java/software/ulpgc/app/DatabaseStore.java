package software.ulpgc.app;

import software.ulpgc.architecture.io.Store;
import software.ulpgc.architecture.model.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public Stream<Game> games(String name, Integer year) {
        try {
            return gamesIn(resultSet(name, year));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSet resultSet() throws SQLException {
        return connection.createStatement().executeQuery("SELECT * FROM games");
    }

    private ResultSet resultSet(String name, Integer year) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM games WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            query.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }

        if (year != null) {
            query.append(" AND year = ?");
            params.add(year);
        }

        PreparedStatement statement = connection.prepareStatement(query.toString());
        for (int i = 0; i < params.size(); i++) {
            statement.setObject(i + 1, params.get(i));
        }

        return statement.executeQuery();
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
