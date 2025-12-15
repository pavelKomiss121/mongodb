package ru.mentee.power.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoConfig {

    private static final String PROPERTIES_FILE = "mongodb.properties";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            Properties props = loadProperties();
            String connectionString =
                    props.getProperty("mongodb.connection.string", "mongodb://localhost:27017");
            mongoClient = MongoClients.create(connectionString);
            log.info("MongoDB client created with connection: {}", connectionString);
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            Properties props = loadProperties();
            String dbName = props.getProperty("mongodb.database.name", "cms");
            database = getMongoClient().getDatabase(dbName);
            log.info("MongoDB database '{}' initialized", dbName);
        }
        return database;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input =
                MongoConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                log.warn("Properties file '{}' not found, using defaults", PROPERTIES_FILE);
            }
        } catch (IOException e) {
            log.error("Error loading properties file", e);
        }
        return props;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            log.info("MongoDB client closed");
        }
    }
}
