package com.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader extends Properties {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
    private static final PropertiesLoader propertiesLoader = new PropertiesLoader();

    static {
        try (InputStream input = new FileInputStream("application.properties")) {
            propertiesLoader.load(input);
        } catch (IOException e) {
            logger.warn("Ошибка чтения файла application.properties");
            throw new RuntimeException(e);
        }
    }

    private PropertiesLoader() {
    }

    public static PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }
}
