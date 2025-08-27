package ru.yandex.practicum.client;

import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public interface StatsClient {

    void registerUserAction(long eventId, long userId, ActionTypeProto actionType, Instant instant);

    Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults);

    Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults);

    Stream<RecommendedEventProto> getEventsInteractionsCount(List<Long> eventIds);
}
