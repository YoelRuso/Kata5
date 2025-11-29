package software.ulpgc.app;

import software.ulpgc.architecture.model.Game;

public class GameDeserializer {

    public static Game fromCsv(String s) {return fromCsv(s.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));}

    private static Game fromCsv(String[] split) {
        return new Game(split[1], split[2], toInt(split[3]));

    }

    private static int toInt(String s) {
        if (s.isEmpty() || s.equals("\\N") || s.equalsIgnoreCase("N/A")) {
            return -1;
        }
        return Integer.parseInt(s);
    }
}
