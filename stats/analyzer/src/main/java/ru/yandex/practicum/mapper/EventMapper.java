package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProto;
import ru.yandex.practicum.model.EventSimilarity;
import ru.yandex.practicum.model.RecommendedEvent;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

@Component
public class EventMapper {
    public EventSimilarity mapToEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .aeventId(eventSimilarityAvro.getEventA())
                .beventId(eventSimilarityAvro.getEventB())
                .score(eventSimilarityAvro.getScore())
                .build();
    }
    
    public RecommendedEventProto mapToRecommendedEventProto(RecommendedEvent recommendedEvent) {
        return RecommendedEventProto.newBuilder()
                .setEventId(recommendedEvent.getEventId())
                .setScore(recommendedEvent.getScore())
                .build();
    }
}