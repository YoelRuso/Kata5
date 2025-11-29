package software.ulpgc.app.messi;

import software.ulpgc.app.Desktop;
import software.ulpgc.app.GameDeserializer;
import software.ulpgc.app.RemoteStore;

public class Main {

    public static void main(String[] args) {

        Desktop.with(new RemoteStore(GameDeserializer::fromCsv))
                .display()
                .setVisible(true);
    }
}
