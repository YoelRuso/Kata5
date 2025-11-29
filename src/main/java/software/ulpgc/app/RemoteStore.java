package software.ulpgc.app;

import software.ulpgc.architecture.io.Store;
import software.ulpgc.architecture.model.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.stream.Stream;


public class RemoteStore implements Store {

    private static final String url = "https://gist.githubusercontent.com/designernatan/27da044c6dc823f7ac7fe3a01f4513ed/raw/d15b5c7d7a5efb38750b16ec935fc126ec9a6e79/vgsales.csv";
    private final Function<String, Game> deserializer;


    public RemoteStore(Function<String, Game> deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public Stream<Game> games() {
        try {
            return loadFrom(new URL(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Game> loadFrom(URL url) throws IOException {
        return loadFrom(url.openConnection());
    }

    private Stream<Game> loadFrom(URLConnection urlConnection) throws IOException {
        return loadFrom(urlConnection.getInputStream());
    }

    private Stream<Game> loadFrom(InputStream inputStream) throws IOException {
        return loadAll(toReader(inputStream))
                .onClose(() -> close(inputStream));
    }

    private BufferedReader toReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private Stream<Game> loadAll(BufferedReader reader) {
        return reader.lines()
                .skip(1)
                .map(deserializer);
    }

    private void close(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}