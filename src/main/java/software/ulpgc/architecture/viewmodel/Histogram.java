package software.ulpgc.architecture.viewmodel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Histogram implements Iterable<Integer>{

    private final Map<Integer,Integer> map;
    private final Map<String, String> labels;

    public Histogram(Map<String, String> labels) {
        this.labels = labels;
        this.map = new HashMap<>();
    }

    public void add(int bin) {map.put(bin, count(bin) + 1);}

    public Integer count(int bin) {
        return map.getOrDefault(bin, 0);
    }

    public int size() {
        return map.size();
    }

    @Override
    public Iterator<Integer> iterator() {
        return map.keySet().iterator();

    }
    public String title() {
        return labels.getOrDefault("title", "");
    }

    public String x() {
        return labels.getOrDefault("x", "");
    }

    public String legend() {
        return labels.getOrDefault("legend", "");
    }
}
