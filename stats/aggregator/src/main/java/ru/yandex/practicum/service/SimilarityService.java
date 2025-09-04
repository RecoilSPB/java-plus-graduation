package ru.yandex.practicum.service;

import ru.yandex.practicum.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.stats.avro.UserActionAvro;

import java.util.List;

public interface SimilarityService {
    List<EventSimilarityAvro> updateSimilarity(UserActionAvro userAction);

    void collectEventSimilarity(EventSimilarityAvro eventSimilarityAvro);

    default void close() {

    }
}
