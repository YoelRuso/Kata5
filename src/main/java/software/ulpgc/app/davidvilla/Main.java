package software.ulpgc.app.davidvilla;

import software.ulpgc.app.*;
import software.ulpgc.architecture.io.Store;
import software.ulpgc.architecture.model.Game;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Main {

    private static final String database = "movies.db";

    public static void main(String[] args) throws SQLException {
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:" + database)) {
            connection.setAutoCommit(false);
            Desktop.with(moviesIn(connection))
                    .display()
                    .setVisible(true);
        }
    }

    private static Store moviesIn(Connection connection) throws SQLException {
        if(isDatabaseEmpty()) importGamesInto(connection);
        return new DatabaseStore(connection);
    }

    private static void importGamesInto(Connection connection) throws SQLException {
        Stream<Game> games = new RemoteStore(GameDeserializer::fromCsv).games();
        new DatabaseRecorder(connection).record(games);
    }

    private static boolean isDatabaseEmpty() {
        return new File(database).length() == 0;
    }
}
