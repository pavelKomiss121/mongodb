package ru.mentee.power.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorStats {
    private String authorId;
    private String authorName;
    private int articlesCount;
    private long totalViews;
    private double avgViews;
}
