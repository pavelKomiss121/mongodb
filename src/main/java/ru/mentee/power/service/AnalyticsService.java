package ru.mentee.power.service;

import java.util.List;
import ru.mentee.power.model.AuthorStats;
import ru.mentee.power.model.ContentReport;
import ru.mentee.power.model.PopularContent;
import ru.mentee.power.model.ReportCriteria;
import ru.mentee.power.model.TimePeriod;
import ru.mentee.power.model.ViewEvent;

/**
 * Сервис аналитики контента.
 */
public interface AnalyticsService {

    /**
     * Получает популярный контент за период.
     *
     * @param period временной период
     * @param limit количество результатов
     * @return список популярного контента
     */
    List<PopularContent> getPopularContent(TimePeriod period, int limit);

    /**
     * Получает статистику по авторам.
     *
     * @param authorIds список ID авторов (null = все)
     * @return статистика авторов
     */
    List<AuthorStats> getAuthorStatistics(List<String> authorIds);

    /**
     * Генерирует отчет по контенту.
     *
     * @param criteria критерии отчета
     * @return сгенерированный отчет
     */
    ContentReport generateReport(ReportCriteria criteria);

    /**
     * Отслеживает событие просмотра контента.
     *
     * @param contentId ID контента
     * @param viewEvent данные о просмотре
     */
    void trackView(String contentId, ViewEvent viewEvent);
}
