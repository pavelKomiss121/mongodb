package ru.mentee.power.model;

import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentVersion {
    private ObjectId id;
    private String contentId;
    private Map<String, Object> changes;
    private String authorId;
    private Date timestamp;
}
