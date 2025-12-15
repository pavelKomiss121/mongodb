package ru.mentee.power.repository;

import java.util.List;
import java.util.Optional;
import ru.mentee.power.model.Content;
import ru.mentee.power.model.ContentVersion;
import ru.mentee.power.model.GeoContent;
import ru.mentee.power.model.GeoPoint;
import ru.mentee.power.model.SearchOptions;

/**
 * Репозиторий для работы с контентом в MongoDB.
 */
public interface ContentRepository {

    /**
     * Сохраняет контент (создает или обновляет).
     *
     * @param content контент для сохранения
     * @return сохраненный контент с ID
     */
    Content save(Content content);

    /**
     * Находит контент по ID.
     *
     * @param id идентификатор контента
     * @return Optional с контентом
     */
    Optional<Content> findById(String id);

    /**
     * Поиск контента по тексту.
     *
     * @param searchText текст для поиска
     * @param options опции поиска (лимит, сортировка)
     * @return список найденного контента
     */
    List<Content> searchByText(String searchText, SearchOptions options);

    /**
     * Поиск контента рядом с геолокацией.
     *
     * @param location точка поиска
     * @param maxDistance максимальное расстояние в метрах
     * @return список контента с расстоянием
     */
    List<GeoContent> findNearby(GeoPoint location, int maxDistance);

    /**
     * Создает новую версию контента.
     *
     * @param contentId ID контента
     * @param newVersion новая версия
     * @return созданная версия
     */
    ContentVersion createVersion(String contentId, Content newVersion);

    /**
     * Получает историю версий контента.
     *
     * @param contentId ID контента
     * @return список версий
     */
    List<ContentVersion> getVersionHistory(String contentId);

    /**
     * Создает текстовый индекс для полнотекстового поиска.
     */
    void createTextIndex();
}
