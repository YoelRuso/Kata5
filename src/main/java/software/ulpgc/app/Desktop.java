package software.ulpgc.app;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import software.ulpgc.architecture.io.Store;
import software.ulpgc.architecture.model.Game;
import software.ulpgc.architecture.viewmodel.Histogram;
import software.ulpgc.architecture.tasks.HistogramBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

public class Desktop extends JFrame {

    private final Store store;

    private Desktop(Store store) {
        this.setTitle("Histogram Display");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.store = store;
    }

    public static Desktop with(Store store){
        return new Desktop(store);
    }

    public Desktop display(){
        display(histogramOf(games()));
        return this;
    }

    public void display(Histogram histogram) {
        this.getContentPane().add(displayOf(histogram));
        this.revalidate();
    }

    private Component displayOf(Histogram histogram) {
        return new ChartPanel(decorate(chartOf(histogram)));
    }

    private JFreeChart decorate(JFreeChart chart) {

        return chart;
    }

    private JFreeChart chartOf(Histogram histogram) {
        return ChartFactory.createHistogram(
                histogram.title(),
                histogram.x(),
                "count",
                datasetOf(histogram)
        );
    }

    private XYSeriesCollection datasetOf(Histogram histogram) {
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(seriesOf(histogram));
        return collection;
    }

    private XYSeries seriesOf(Histogram histogram) {
        XYSeries series = new XYSeries(histogram.legend());
        for (int bin : histogram)
            series.add(bin, histogram.count(bin));
        return series;
    }

    private static Stream<Game> games() {
        return new RemoteStore(GameDeserializer::fromCsv)
                .games()
                .filter(m -> m.year() >= 1990)
                .filter(m -> m.year() <= 2018);
    }

    private static Histogram histogramOf(Stream<Game> games) {
        return HistogramBuilder.with(games)
                .title("Juegos por años")
                .x("Año")
                .legend("Nº Juegos")
                .build(Game::year);
    }

    public Store store() {
        return store;
    }
}