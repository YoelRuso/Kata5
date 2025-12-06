package software.ulpgc.app.api;

import io.javalin.Javalin;
import software.ulpgc.app.DatabaseRecorder;
import software.ulpgc.app.DatabaseStore;
import software.ulpgc.app.GameDeserializer;
import software.ulpgc.app.RemoteStore;
import software.ulpgc.architecture.io.Store;
import software.ulpgc.architecture.model.Game;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final String database = "games.db";

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + database);
        connection.setAutoCommit(false);
        Store store = gamesIn(connection);

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        }).start(7070);

        app.get("/", ctx -> ctx.result("Games API is running! Use /api/games to get all games"));

        app.get("/api/games", ctx -> {
            try {
                List<Game> games = store.games().collect(Collectors.toList());
                ctx.json(games);
            } catch (Exception e) {
                ctx.status(500).result("Error retrieving games: " + e.getMessage());
            }
        });

        System.out.println("Server started on http://localhost:7070");
        System.out.println("Access games at http://localhost:7070/api/games");
    }

    private static Store gamesIn(Connection connection) throws SQLException {
        if (isDatabaseEmpty()) importGamesInto(connection);
        return new DatabaseStore(connection);
    }

    private static void importGamesInto(Connection connection) throws SQLException {
        Stream<Game> games = new RemoteStore(GameDeserializer::fromCsv).games();
        new DatabaseRecorder(connection).record(games);
    }

    private static boolean isDatabaseEmpty() {
        File dbFile = new File(database);
        return !dbFile.exists() || dbFile.length() == 0;
    }
}
