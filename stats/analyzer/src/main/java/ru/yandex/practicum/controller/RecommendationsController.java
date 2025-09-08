package ru.yandex.practicum.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.stats.analyzer.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.request.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.request.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.request.UserPredictionsRequestProto;
import ru.yandex.practicum.service.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("RecommendationsController call getRecommendationsForUser for request = {}", request);
            List<RecommendedEventProto> recommendedEvents = recommendationService.generateRecommendationsForUser(request);
            for (RecommendedEventProto event : recommendedEvents) {
                responseObserver.onNext(event);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            // отправляем ошибку клиенту
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("RecommendationsController call getSimilarEvents for request = {}", request);
            List<RecommendedEventProto> recommendedEvents = recommendationService.getSimilarEvents(request);
            for (RecommendedEventProto event : recommendedEvents) {
                responseObserver.onNext(event);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            // отправляем ошибку клиенту
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("RecommendationsController call getInteractionsCount for request = {}", request);
            List<RecommendedEventProto> recommendedEvents = recommendationService.getInteractionsCount(request);
            for (RecommendedEventProto event : recommendedEvents) {
                responseObserver.onNext(event);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            // отправляем ошибку клиенту
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}
