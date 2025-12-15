package ru.mentee.power.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mentee.power.model.Content;
import ru.mentee.power.repository.ContentRepository;

@Slf4j
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public Content save(Content content) {
        return contentRepository.save(content);
    }

    public Optional<Content> findById(String id) {
        return contentRepository.findById(id);
    }

    public List<Content> searchByText(String searchText) {
        return contentRepository.searchByText(
                searchText, ru.mentee.power.model.SearchOptions.builder().limit(10).build());
    }
}
