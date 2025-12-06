package software.ulpgc.app.api;

import io.javalin.Javalin;
import software.ulpgc.app.GameDeserializer;
import software.ulpgc.app.RemoteStore;
import software.ulpgc.architecture.io.Store;
import software.ulpgc.architecture.model.Game;

import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Store store = new RemoteStore(GameDeserializer::fromCsv);
        
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
}
