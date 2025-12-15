package ru.mentee.power.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private ObjectId id;
    private String title;
    private String type;
    private String status;
    private Author author;
    private List<String> tags;
    private List<String> categories;
    private Metadata metadata;
    private Location location;
    private Date createdAt;
    private Date updatedAt;
}
