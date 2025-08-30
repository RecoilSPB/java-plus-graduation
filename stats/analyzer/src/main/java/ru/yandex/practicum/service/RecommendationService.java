package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.stats.request.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.request.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.request.UserPredictionsRequestProto;
import ru.yandex.practicum.stats.avro.UserActionAvro;

import java.util.List;

public interface RecommendationService {

    List<RecommendedEventProto> generateRecommendationsForUser(UserPredictionsRequestProto request);

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);

    void saveUserAction(UserActionAvro userActionAvro);
}