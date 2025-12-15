package ru.mentee.power.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Article extends Content {
    private String body;
    private int readingTime;
    private List<String> tableOfContents;

    public Article(
            ObjectId id,
            String title,
            String type,
            String status,
            Author author,
            List<String> tags,
            List<String> categories,
            Metadata metadata,
            Location location,
            Date createdAt,
            Date updatedAt,
            String body,
            int readingTime,
            List<String> tableOfContents) {
        super(
                id,
                title,
                type,
                status,
                author,
                tags,
                categories,
                metadata,
                location,
                createdAt,
                updatedAt);
        this.body = body;
        this.readingTime = readingTime;
        this.tableOfContents = tableOfContents;
    }
}
