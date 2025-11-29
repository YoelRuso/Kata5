package software.ulpgc.architecture.tasks;

import software.ulpgc.architecture.model.Game;
import software.ulpgc.architecture.viewmodel.Histogram;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class HistogramBuilder {

    private final Stream<Game> games;
    private final Map<String, String>  labels;

    private HistogramBuilder(Stream<Game> games) {
        this.games = games;
        this.labels = new HashMap<>();
    }
    public static HistogramBuilder with(Stream<Game> games) {
        return new HistogramBuilder(games);
    }

    public HistogramBuilder title(String title) {
        labels.put("title", title);
        return this;
    }

    public HistogramBuilder x(String x) {
        this.labels.put("x", x);
        return this;
    }

    public HistogramBuilder legend(String legend) {
        this.labels.put("legend", legend);
        return this;
    }

    public Histogram build(Function<Game, Integer> binarize) {
        Histogram histogram = new Histogram(labels);
        games.map(binarize).forEach(histogram::add);
        return histogram;
    }


}
