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
public class Video extends Content {
    private String videoUrl;
    private int duration;
    private String previewUrl;

    public Video(
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
            String videoUrl,
            int duration,
            String previewUrl) {
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
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.previewUrl = previewUrl;
    }
}
