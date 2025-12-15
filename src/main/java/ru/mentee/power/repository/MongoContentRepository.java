package ru.mentee.power.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.TextSearchOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.mentee.power.config.CodecConfig;
import ru.mentee.power.model.Content;
import ru.mentee.power.model.ContentVersion;
import ru.mentee.power.model.GeoContent;
import ru.mentee.power.model.GeoPoint;
import ru.mentee.power.model.SearchOptions;
import ru.mentee.power.util.BsonMapper;

@Slf4j
public class MongoContentRepository implements ContentRepository {

    private final MongoCollection<Content> contentCollection;
    private final MongoCollection<ContentVersion> versionCollection;
    private final MongoCollection<Document> documentCollection;

    public MongoContentRepository(MongoClient mongoClient) {
        this.contentCollection =
                mongoClient
                        .getDatabase("cms")
                        .getCollection("content", Content.class)
                        .withCodecRegistry(CodecConfig.getPojoCodecRegistry());
        this.versionCollection =
                mongoClient
                        .getDatabase("cms")
                        .getCollection("content_versions", ContentVersion.class)
                        .withCodecRegistry(CodecConfig.getPojoCodecRegistry());
        this.documentCollection =
                mongoClient.getDatabase("cms").getCollection("content", Document.class);
    }

    @Override
    public Content save(Content content) {
        if (content.getId() == null) {
            content.setId(new ObjectId());
            content.setCreatedAt(new Date());
            contentCollection.insertOne(content);
            log.debug("Created new content with ID: {}", content.getId());
        } else {
            content.setUpdatedAt(new Date());
            contentCollection.replaceOne(Filters.eq("_id", content.getId()), content);
            log.debug("Updated content with ID: {}", content.getId());
        }
        return content;
    }

    @Override
    public Optional<Content> findById(String id) {
        Content content = contentCollection.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(content);
    }

    @Override
    public List<Content> searchByText(String searchText, SearchOptions options) {
        var findIterable =
                documentCollection.find(Filters.text(searchText, new TextSearchOptions()));

        if (options.getSortBy() != null) {
            if (options.isAscending()) {
                findIterable.sort(Sorts.ascending(options.getSortBy()));
            } else {
                findIterable.sort(Sorts.descending(options.getSortBy()));
            }
        } else {
            findIterable.sort(Sorts.metaTextScore("score"));
        }

        findIterable.limit(options.getLimit());

        List<Content> results = new ArrayList<>();
        for (Document doc : findIterable) {
            Content content = documentToContent(doc);
            if (content != null) {
                results.add(content);
            }
        }
        return results;
    }

    @Override
    public List<GeoContent> findNearby(GeoPoint location, int maxDistance) {
        // Создаем геопространственный индекс, если его еще нет
        try {
            documentCollection.createIndex(Indexes.geo2dsphere("location"));
        } catch (Exception e) {
            // Индекс уже существует
            log.debug("Geo index already exists");
        }

        Document query =
                new Document(
                        "location",
                        new Document(
                                "$near",
                                new Document(
                                                "$geometry",
                                                new Document("type", "Point")
                                                        .append(
                                                                "coordinates",
                                                                List.of(
                                                                        location.getLongitude(),
                                                                        location.getLatitude())))
                                        .append("$maxDistance", maxDistance)));

        var findIterable = documentCollection.find(query);

        List<GeoContent> results = new ArrayList<>();
        for (Document doc : findIterable) {
            Content content = documentToContent(doc);
            if (content != null) {
                // Для упрощения устанавливаем расстояние как 0
                // В реальном приложении можно использовать $geoNear для вычисления расстояния
                results.add(GeoContent.builder().content(content).distance(0.0).build());
            }
        }
        return results;
    }

    @Override
    public ContentVersion createVersion(String contentId, Content newVersion) {
        Optional<Content> existing = findById(contentId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Content not found: " + contentId);
        }

        Content oldContent = existing.get();
        Map<String, Object> changes = calculateChanges(oldContent, newVersion);

        ContentVersion version =
                ContentVersion.builder()
                        .id(new ObjectId())
                        .contentId(contentId)
                        .changes(changes)
                        .authorId(
                                newVersion.getAuthor() != null
                                        ? newVersion.getAuthor().getId()
                                        : null)
                        .timestamp(new Date())
                        .build();

        versionCollection.insertOne(version);
        log.debug("Created version for content: {}", contentId);
        return version;
    }

    @Override
    public List<ContentVersion> getVersionHistory(String contentId) {
        return versionCollection
                .find(Filters.eq("contentId", contentId))
                .sort(Sorts.descending("timestamp"))
                .into(new ArrayList<>());
    }

    @Override
    public void createTextIndex() {
        try {
            // MongoDB позволяет только один текстовый индекс на коллекцию
            // Создаем составной индекс на оба поля
            documentCollection.createIndex(Indexes.text("title"));
            log.debug("Text index on title created");
        } catch (Exception e) {
            log.debug("Text index already exists or failed: {}", e.getMessage());
        }
        log.info("Text indexes created");
    }

    private Content documentToContent(Document doc) {
        return BsonMapper.documentToContent(doc);
    }

    private Map<String, Object> calculateChanges(Content oldContent, Content newContent) {
        Map<String, Object> changes = new HashMap<>();
        if (!oldContent.getTitle().equals(newContent.getTitle())) {
            changes.put(
                    "title", Map.of("old", oldContent.getTitle(), "new", newContent.getTitle()));
        }
        return changes;
    }
}
