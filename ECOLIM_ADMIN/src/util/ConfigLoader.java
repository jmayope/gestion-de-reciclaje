package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties props = new Properties();
    private static boolean cargado = false;

    private static void cargar() {

        if (cargado) {
            return;
        }

        try (FileInputStream fis = new FileInputStream("config.properties")) {

            props.load(fis);
            cargado = true;

        } catch (IOException e) {
            System.out.println("Error cargando config.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        cargar();
        return props.getProperty(key);
    }
}