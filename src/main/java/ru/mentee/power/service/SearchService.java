package ru.mentee.power.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mentee.power.model.Content;
import ru.mentee.power.model.GeoContent;
import ru.mentee.power.model.GeoPoint;
import ru.mentee.power.model.SearchOptions;
import ru.mentee.power.repository.ContentRepository;

@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final ContentRepository contentRepository;

    public List<Content> searchByText(String searchText, SearchOptions options) {
        return contentRepository.searchByText(searchText, options);
    }

    public List<GeoContent> findNearby(GeoPoint location, int maxDistance) {
        return contentRepository.findNearby(location, maxDistance);
    }
}
