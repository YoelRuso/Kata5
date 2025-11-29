package software.ulpgc.architecture.io;

import software.ulpgc.architecture.model.Game;

import java.util.stream.Stream;

public interface Recorder {
    void record(Stream<Game> games);
}
