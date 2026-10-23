package com.team.expense.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DbConnection {
    private static final Path CONFIG_PATH = Path.of("db.properties");
    private static final Properties PROPERTIES = loadProperties();

    private DbConnection() {}

    private static Properties loadProperties() {
        Properties properties = new Properties();
        if (Files.isRegularFile(CONFIG_PATH)) {
            try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
                properties.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Could not read db.properties.", e);
            }
        }
        return properties;
    }

    public static boolean isConfigured() {
        return hasText(PROPERTIES.getProperty("db.url"))
                && hasText(PROPERTIES.getProperty("db.username"));
    }

    public static Connection getConnection() throws SQLException {
        if (!isConfigured()) {
            throw new SQLException("Database is not configured. Create db.properties from db.properties.example.");
        }
        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url").trim(),
                PROPERTIES.getProperty("db.username").trim(),
                PROPERTIES.getProperty("db.password", ""));
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
