package ru.mentee.power.util;

import java.util.List;
import org.bson.Document;
import ru.mentee.power.model.Author;
import ru.mentee.power.model.Content;
import ru.mentee.power.model.Location;
import ru.mentee.power.model.Metadata;

public class BsonMapper {

    /**
     * Преобразует Document в Content.
     */
    public static Content documentToContent(Document doc) {
        if (doc == null) {
            return null;
        }

        Content content = new Content();
        content.setId(doc.getObjectId("_id"));
        content.setTitle(doc.getString("title"));
        content.setType(doc.getString("type"));
        content.setStatus(doc.getString("status"));

        Document authorDoc = doc.get("author", Document.class);
        if (authorDoc != null) {
            content.setAuthor(
                    Author.builder()
                            .id(authorDoc.getString("id"))
                            .name(authorDoc.getString("name"))
                            .avatar(authorDoc.getString("avatar"))
                            .build());
        }

        content.setTags(doc.getList("tags", String.class));
        content.setCategories(doc.getList("categories", String.class));

        Document metadataDoc = doc.get("metadata", Document.class);
        if (metadataDoc != null) {
            content.setMetadata(
                    Metadata.builder()
                            .views(metadataDoc.getLong("views"))
                            .likes(metadataDoc.getInteger("likes"))
                            .shares(metadataDoc.getInteger("shares"))
                            .build());
        }

        Document locationDoc = doc.get("location", Document.class);
        if (locationDoc != null) {
            List<Double> coords = locationDoc.getList("coordinates", Double.class);
            if (coords != null && coords.size() >= 2) {
                content.setLocation(
                        Location.builder()
                                .type(locationDoc.getString("type"))
                                .coordinates(coords)
                                .build());
            }
        }

        content.setCreatedAt(doc.getDate("createdAt"));
        content.setUpdatedAt(doc.getDate("updatedAt"));

        return content;
    }

    /**
     * Преобразует Content в Document.
     */
    public static Document contentToDocument(Content content) {
        if (content == null) {
            return null;
        }

        Document doc = new Document();
        if (content.getId() != null) {
            doc.append("_id", content.getId());
        }
        doc.append("title", content.getTitle())
                .append("type", content.getType())
                .append("status", content.getStatus());

        if (content.getAuthor() != null) {
            doc.append(
                    "author",
                    new Document("id", content.getAuthor().getId())
                            .append("name", content.getAuthor().getName())
                            .append("avatar", content.getAuthor().getAvatar()));
        }

        if (content.getTags() != null) {
            doc.append("tags", content.getTags());
        }
        if (content.getCategories() != null) {
            doc.append("categories", content.getCategories());
        }

        if (content.getMetadata() != null) {
            doc.append(
                    "metadata",
                    new Document("views", content.getMetadata().getViews())
                            .append("likes", content.getMetadata().getLikes())
                            .append("shares", content.getMetadata().getShares()));
        }

        if (content.getLocation() != null && content.getLocation().getCoordinates() != null) {
            doc.append(
                    "location",
                    new Document("type", content.getLocation().getType())
                            .append("coordinates", content.getLocation().getCoordinates()));
        }

        if (content.getCreatedAt() != null) {
            doc.append("createdAt", content.getCreatedAt());
        }
        if (content.getUpdatedAt() != null) {
            doc.append("updatedAt", content.getUpdatedAt());
        }

        return doc;
    }
}
