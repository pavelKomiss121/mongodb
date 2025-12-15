package ru.mentee.power.util;

import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

@Slf4j
public class MongoUtils {

    /**
     * Проверяет подключение к MongoDB.
     */
    public static boolean checkConnection(MongoCollection<Document> collection) {
        try {
            collection.countDocuments();
            return true;
        } catch (Exception e) {
            log.error("MongoDB connection check failed", e);
            return false;
        }
    }

    /**
     * Очищает коллекцию.
     */
    public static void clearCollection(MongoCollection<Document> collection) {
        collection.deleteMany(new Document());
        log.info("Collection cleared");
    }

    /**
     * Получает количество документов в коллекции.
     */
    public static long getCollectionSize(MongoCollection<Document> collection) {
        return collection.countDocuments();
    }
}
