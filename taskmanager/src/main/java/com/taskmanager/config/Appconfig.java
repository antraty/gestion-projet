package com.taskmanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    public static void load() {
        try (InputStream in = AppConfig.class.getResourceAsStream("/config/application.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("application.properties not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot load app config", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String getOrDefault(String key, String def) {
        return props.getProperty(key, def);
    }
}