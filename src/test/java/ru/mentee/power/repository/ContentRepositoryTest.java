package ru.mentee.power.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.power.model.Article;
import ru.mentee.power.model.Author;
import ru.mentee.power.model.Content;
import ru.mentee.power.model.GeoContent;
import ru.mentee.power.model.GeoPoint;
import ru.mentee.power.model.Location;
import ru.mentee.power.model.Metadata;
import ru.mentee.power.model.SearchOptions;

@DisplayName("Тестирование ContentRepository")
@Testcontainers
class ContentRepositoryTest {

    @Container static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:7");

    private ContentRepository repository;
    private MongoClient mongoClient;

    @BeforeEach
    void setUp() {
        mongoContainer.start();
        String connectionString = mongoContainer.getConnectionString();
        mongoClient = MongoClients.create(connectionString);
        repository = new MongoContentRepository(mongoClient);
    }

    @AfterEach
    void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    @DisplayName("Should сохранить и найти статью")
    void shouldSaveAndFindArticle() {
        // Given
        Article article = new Article();
        article.setTitle("Тестовая статья");
        article.setType("article");
        article.setStatus("published");
        article.setBody("Текст статьи");
        article.setReadingTime(5);
        article.setAuthor(new Author("author1", "Иван Петров", null));
        article.setTags(List.of("test", "mongodb"));
        article.setMetadata(new Metadata(0, 0, 0));

        // When
        Content saved = repository.save(article);
        Optional<Content> found = repository.findById(saved.getId().toString());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Тестовая статья");
    }

    @Test
    @DisplayName("Should найти контент по геолокации")
    void shouldFindNearbyContent() {
        // Given - создаем контент с геолокацией
        for (int i = 0; i < 5; i++) {
            Article article =
                    createArticleWithLocation(
                            55.7558 + (i * 0.01), // широта
                            37.6173 + (i * 0.01) // долгота
                            );
            repository.save(article);
        }

        // When - ищем рядом с центром Москвы
        GeoPoint moscow = new GeoPoint(37.6173, 55.7558);
        List<GeoContent> nearby = repository.findNearby(moscow, 5000);

        // Then
        assertThat(nearby).isNotEmpty();
    }

    @Test
    @DisplayName("Should выполнить полнотекстовый поиск")
    void shouldPerformTextSearch() {
        // Given - создаем индекс и контент
        repository.createTextIndex();

        Article article1 = new Article();
        article1.setTitle("MongoDB для начинающих");
        article1.setType("article");
        article1.setStatus("published");
        article1.setBody("MongoDB - это NoSQL база данных");
        article1.setReadingTime(3);
        article1.setMetadata(new Metadata(0, 0, 0));
        repository.save(article1);

        Article article2 = new Article();
        article2.setTitle("PostgreSQL vs MongoDB");
        article2.setType("article");
        article2.setStatus("published");
        article2.setBody("Сравнение реляционных и документных БД");
        article2.setReadingTime(5);
        article2.setMetadata(new Metadata(0, 0, 0));
        repository.save(article2);

        // When
        List<Content> results =
                repository.searchByText("MongoDB", SearchOptions.builder().limit(10).build());

        // Then
        assertThat(results).isNotEmpty();
    }

    private Article createArticleWithLocation(double latitude, double longitude) {
        Article article = new Article();
        article.setTitle("Article with location");
        article.setType("article");
        article.setStatus("published");
        article.setBody("Content");
        article.setReadingTime(3);
        article.setLocation(
                Location.builder().type("Point").coordinates(List.of(longitude, latitude)).build());
        article.setMetadata(new Metadata(0, 0, 0));
        return article;
    }
}
