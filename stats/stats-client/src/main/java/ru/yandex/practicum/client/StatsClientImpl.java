package ru.yandex.practicum.client;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.grpc.stats.analyzer.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.stats.request.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.request.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.request.UserPredictionsRequestProto;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Клиент для работы со статистикой и рекомендациями
 *
 * @author PopovN
 * @created 09.06.2025 14:13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub userActionClient;

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub recommendationsClient;

    @Override
    public void registerUserAction(long eventId, long userId, ActionTypeProto actionType, Instant timestamp) {
        log.info("Registering user action: eventId={}, userId={}, actionType={}, time={}",
                eventId, userId, actionType, timestamp);

        Timestamp protoTimestamp = convertToProtoTimestamp(timestamp);

        UserActionProto request = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setActionType(actionType)
                .setTimestamp(protoTimestamp)
                .build();

        log.debug("Sending user action request: {}", request);
        userActionClient.collectUserAction(request);
    }

    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        log.debug("Getting similar events: eventId={}, userId={}, maxResults={}",
                eventId, userId, maxResults);

        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        return convertToStream(recommendationsClient.getSimilarEvents(request));
    }

    @Override
    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        log.debug("Getting user recommendations: userId={}, maxResults={}",
                userId, maxResults);

        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        return convertToStream(recommendationsClient.getRecommendationsForUser(request));
    }

    @Override
    public Stream<RecommendedEventProto> getEventsInteractionsCount(List<Long> eventIds) {
        log.debug("Getting interactions count for {} events", eventIds.size());

        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        return convertToStream(recommendationsClient.getInteractionsCount(request));
    }

    private Timestamp convertToProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private Stream<RecommendedEventProto> convertToStream(Iterator<RecommendedEventProto> iterator) {
        Spliterator<RecommendedEventProto> spliterator =
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);

        return StreamSupport.stream(spliterator, false);
    }
}