package com.gp16.event.util;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    public static MongoDatabase connect() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new RuntimeException("config.properties not found");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }

        String uri = props.getProperty("connectionString");
        String dbName = props.getProperty("databaseName");
        return MongoClients.create(uri).getDatabase(dbName);
    }
}
