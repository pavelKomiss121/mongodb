package ru.mentee.power.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;

@Slf4j
public class AggregationRepository {

    private final MongoCollection<Document> contentCollection;

    public AggregationRepository(MongoCollection<Document> contentCollection) {
        this.contentCollection = contentCollection;
    }

    /**
     * Получить статистику по авторам
     */
    public List<Document> getAuthorStatistics() {
        List<Bson> pipeline =
                Arrays.asList(
                        // Фильтруем только опубликованные статьи
                        Aggregates.match(Filters.eq("status", "published")),

                        // Группируем по автору
                        Aggregates.group(
                                "$author.id",
                                Accumulators.first("authorName", "$author.name"),
                                Accumulators.sum("articlesCount", 1),
                                Accumulators.sum("totalViews", "$metadata.views"),
                                Accumulators.avg("avgViews", "$metadata.views")),

                        // Сортируем по количеству просмотров
                        Aggregates.sort(Sorts.descending("totalViews")),

                        // Ограничиваем топ-10
                        Aggregates.limit(10));

        return contentCollection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * Поиск популярного контента по геолокации
     */
    public List<Document> getNearbyPopularContent(
            double longitude, double latitude, int maxDistance) {
        List<Bson> pipeline =
                Arrays.asList(
                        // Геопространственный поиск
                        Aggregates.match(
                                new Document(
                                        "location",
                                        new Document(
                                                "$near",
                                                new Document(
                                                                "$geometry",
                                                                new Document("type", "Point")
                                                                        .append(
                                                                                "coordinates",
                                                                                Arrays.asList(
                                                                                        longitude,
                                                                                        latitude)))
                                                        .append("$maxDistance", maxDistance)))),

                        // Фильтруем популярный контент
                        Aggregates.match(Filters.gte("metadata.views", 100)),

                        // Добавляем рейтинг популярности
                        Aggregates.addFields(
                                new Field<>(
                                        "popularityScore",
                                        new Document(
                                                "$multiply",
                                                Arrays.asList(
                                                        "$metadata.views",
                                                        new Document(
                                                                "$divide",
                                                                Arrays.asList(
                                                                        "$metadata.likes",
                                                                        new Document(
                                                                                "$add",
                                                                                Arrays.asList(
                                                                                        "$metadata.views",
                                                                                        1)))))))),

                        // Сортируем по популярности
                        Aggregates.sort(Sorts.descending("popularityScore")),

                        // Ограничиваем результат
                        Aggregates.limit(20));

        return contentCollection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * Фасетный поиск (многомерная агрегация)
     */
    public Document getFacetedSearch(String searchText) {
        List<Bson> pipeline =
                Arrays.asList(
                        // Текстовый поиск
                        Aggregates.match(Filters.text(searchText)),

                        // Фасетная агрегация
                        Aggregates.facet(
                                // Фасет по типам контента
                                new com.mongodb.client.model.Facet(
                                        "byType",
                                        Aggregates.group("$type", Accumulators.sum("count", 1)),
                                        Aggregates.sort(Sorts.descending("count"))),

                                // Фасет по тегам
                                new com.mongodb.client.model.Facet(
                                        "byTags",
                                        Aggregates.unwind("$tags"),
                                        Aggregates.group("$tags", Accumulators.sum("count", 1)),
                                        Aggregates.sort(Sorts.descending("count")),
                                        Aggregates.limit(10)),

                                // Фасет по авторам
                                new com.mongodb.client.model.Facet(
                                        "byAuthors",
                                        Aggregates.group(
                                                "$author.name", Accumulators.sum("count", 1)),
                                        Aggregates.sort(Sorts.descending("count")),
                                        Aggregates.limit(5)),

                                // Результаты поиска
                                new com.mongodb.client.model.Facet(
                                        "results",
                                        Aggregates.sort(Sorts.metaTextScore("score")),
                                        Aggregates.limit(20),
                                        Aggregates.project(
                                                new Document("title", 1)
                                                        .append("type", 1)
                                                        .append("author", 1)
                                                        .append(
                                                                "score",
                                                                new Document(
                                                                        "$meta", "textScore"))))));

        return contentCollection.aggregate(pipeline).first();
    }
}
