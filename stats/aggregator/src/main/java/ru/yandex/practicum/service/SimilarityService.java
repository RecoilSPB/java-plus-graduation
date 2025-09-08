package ru.yandex.practicum.service;

import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

import java.util.List;

public interface SimilarityService {
    List<EventSimilarityAvro> userActionHandle(UserActionAvro userAction);
}
