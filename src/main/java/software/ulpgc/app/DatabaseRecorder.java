package software.ulpgc.app;

import software.ulpgc.architecture.io.Recorder;
import software.ulpgc.architecture.model.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

public class DatabaseRecorder implements Recorder {

    private final Connection connection;
    private final PreparedStatement statement;
    private int count = 0;
    public DatabaseRecorder(Connection connection) throws SQLException {
        this.connection = connection;
        this.createTableIfNotExists();
        this.statement = connection.prepareStatement("INSERT INTO games (name, platform, year) VALUES (?, ?, ?)");
    }
    @Override
    public void record(Stream<Game> games) {
        try {
            games.forEach(this::record);
            flushBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void createTableIfNotExists() throws SQLException {
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS games (name TEXT, platform TEXT, year INTEGER)");
    }
    private void flushBatch() throws SQLException {
        statement.executeBatch();
        connection.commit();
    }
    private void record(Game game) {
        try {
            insert(game);
            flushBatchIfRequired();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void flushBatchIfRequired() throws SQLException {
        if (++count % 10000 == 0) statement.executeBatch();
    }
    private void insert(Game game) throws SQLException {
        statement.setString(1, game.name());
        statement.setString(2, game.platform());
        statement.setInt(3, game.year());
        statement.addBatch();
    }
}
