package ru.mentee.power.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchOptions {
    @Builder.Default private int limit = 10;
    private String sortBy;
    @Builder.Default private boolean ascending = false;
}
