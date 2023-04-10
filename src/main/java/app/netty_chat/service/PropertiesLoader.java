package app.netty_chat.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader extends Properties {

    private static final PropertiesLoader propertiesLoader = new PropertiesLoader();

     static {
        try (InputStream input = new FileInputStream("application.properties")) {
            propertiesLoader.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     }

    private PropertiesLoader () {}

    public static PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }
}
