package ru.mentee.power.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentReport {
    private TimePeriod period;
    private int totalContent;
    private Map<String, Integer> contentByType;
    private List<AuthorStats> topAuthors;
    private List<PopularContent> topContent;
}
