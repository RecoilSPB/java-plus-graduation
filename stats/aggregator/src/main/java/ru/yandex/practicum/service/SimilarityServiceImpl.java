package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.cashe.InteractionCache;
import ru.yandex.practicum.dto.InteractionDto;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SimilarityServiceImpl implements SimilarityService {

    InteractionCache cache;

    @Override
    public List<EventSimilarityAvro> userActionHandle(UserActionAvro userAction) {
        log.info("updateSimilarity for userAction = {}", userAction);

        Instant requestTime = userAction.getTimestamp();
        InteractionDto newInteraction = InteractionDto.builder()
                .userId(userAction.getUserId())
                .eventId(userAction.getEventId())
                .weight(getWeightByActionType(userAction.getActionType()))
                .build();
        return cache.addInteraction(newInteraction) ?
                computeSimilarities(newInteraction.getEventId(),
                        requestTime,
                        newInteraction.getUserId())
                : List.of();
    }

    private double getWeightByActionType(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }

    private List<EventSimilarityAvro> computeSimilarities(long eventId, Instant timestamp, long userId) {
        List<EventSimilarityAvro> result = new ArrayList<>();

        double sA = cache.getEventSum(eventId);
        if (sA <= 0.0) {
            return result; // нет взаимодействий — нечего считать
        }

        // Берём все события, с которыми взаимодействовал пользователь
        Map<Long, Double> userWeights = cache.getUserWeights(userId);
        if (userWeights == null || userWeights.isEmpty()) {
            return result;
        }

        for (Long otherEventId : userWeights.keySet()) {
            if (otherEventId == eventId) {
                continue; // пропускаем совпадение с самим собой
            }

            // Достаём sMin без учёта ориентации
            double sMin = cache.getSMinBetween(eventId, otherEventId);
            if (sMin <= 0.0) {
                continue; // нет общей связи
            }

            double sB = cache.getEventSum(otherEventId);
            if (sB <= 0.0) {
                continue; // событие пустое
            }

            double similarity = sMin / Math.sqrt(sA * sB);

            long eventA = Math.min(eventId, otherEventId);
            long eventB = Math.max(eventId, otherEventId);

            EventSimilarityAvro similarityAvro = EventSimilarityAvro.newBuilder()
                    .setEventA(eventA)
                    .setEventB(eventB)
                    .setScore(similarity)
                    .setTimestamp(timestamp)
                    .build();

            result.add(similarityAvro);
        }

        // сортируем по убыванию score
        result.sort((e1, e2) -> Double.compare(e2.getScore(), e1.getScore()));

        return result;
    }
}