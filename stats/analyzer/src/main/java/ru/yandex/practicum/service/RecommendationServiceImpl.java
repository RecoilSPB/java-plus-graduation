package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.stats.request.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.request.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.request.UserPredictionsRequestProto;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.mapper.UserMapper;
import ru.yandex.practicum.model.EventSimilarity;
import ru.yandex.practicum.model.RecommendedEvent;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.repository.EventSimilarityRepository;
import ru.yandex.practicum.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    static long EVENT_COUNT_PREDICTION = 5;
    EventSimilarityRepository eventSimilarityRepository;
    UserActionRepository userActionRepository;
    UserMapper userMapper;
    EventMapper eventMapper;

    @Override
    public List<RecommendedEventProto> generateRecommendationsForUser(UserPredictionsRequestProto request) {
        List<UserAction> lastUserEvents = userActionRepository.findByUserIdOrderByCreatedDescLimitedTo(
                request.getUserId(), request.getMaxResults()
        );

        if (lastUserEvents.isEmpty()) {
            return emptyList();
        }

        List<RecommendedEvent> recommendedEvents = new ArrayList<>();
        lastUserEvents.forEach(event -> recommendedEvents.addAll(
                getSimilarEvents(request.getUserId(), event.getEventId(), request.getMaxResults())
                        .stream()
                        .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                        .limit(request.getMaxResults())
                        .map(similarEvent -> genRecommendedEventFrom(similarEvent, event.getEventId()))
                        .toList()));

        List<RecommendedEvent> limitRecommendedEvents = recommendedEvents.stream()
                .sorted(Comparator.comparingDouble(RecommendedEvent::getScore).reversed())
                .limit(request.getMaxResults())
                .toList();
        log.info("RecommendedEvents: {}", recommendedEvents);
        limitRecommendedEvents.forEach(
                event -> event.setScore(getPrediction(event.getEventId(), request.getUserId()))
        );
        return limitRecommendedEvents.stream()
                .map(eventMapper::mapToRecommendedEventProto)
                .toList();
    }


    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        return getSimilarEvents(request.getUserId(), request.getEventId(), request.getMaxResults()).stream()
                .map(event -> genRecommendedEventProtoFrom(event, request.getEventId()))
                .toList();
    }


    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        return userActionRepository.getSumWeightForEvents(request.getEventIdList())
                .stream()
                .map(eventMapper::mapToRecommendedEventProto)
                .toList();
    }

    @Override
    public void saveUserAction(UserActionAvro userActionAvro) {
        UserAction userAction = userMapper.mapToUserAction(userActionAvro);
        log.info("call saveUserAction for userActionAvro: {}", userActionAvro);
        Optional<UserAction> oldUserAction = userActionRepository.findByUserIdAndEventId(userAction.getUserId(), userAction.getEventId());
        if (oldUserAction.isPresent()) {
            userAction.setId(oldUserAction.get().getId());
            if (userAction.getWeight() < oldUserAction.get().getWeight()) {
                userAction.setWeight(oldUserAction.get().getWeight());
            }
        }
        log.info("new UserAction is: {}", userAction);
        userActionRepository.save(userAction);
    }

    private RecommendedEventProto genRecommendedEventProtoFrom(EventSimilarity event, Long eventId) {
        Long recommendedEventId = Objects.equals(event.getAeventId(), eventId) ?
                event.getBeventId() : event.getAeventId();

        return RecommendedEventProto.newBuilder()
                .setEventId(recommendedEventId)
                .setScore(event.getScore())
                .build();
    }

    private RecommendedEvent genRecommendedEventFrom(EventSimilarity similarEvent, Long eventId) {
        Long recommendedEventId = Objects.equals(similarEvent.getAeventId(), eventId) ?
                similarEvent.getBeventId() : similarEvent.getAeventId();

        return RecommendedEvent.builder()
                .eventId(recommendedEventId)
                .score(similarEvent.getScore())
                .build();
    }

    private List<EventSimilarity> getSimilarEvents(Long userId, Long eventId, Long limit) {
        List<EventSimilarity> events = eventSimilarityRepository.findAllByEvent(eventId);
        List<Long> actions = userActionRepository.findAllByUserId(userId).stream()
                .map(UserAction::getEventId).toList();

        List<EventSimilarity> result = events.stream()
                .filter(event -> !(actions.contains(event.getAeventId()) && actions.contains(event.getBeventId())))
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(limit)
                .toList();

        log.info("similar events are {}", result);
        return result;
    }

    private double getPrediction(Long eventId, Long userId) {
        double prediction = 0.0;

        Map<Long, Double> ratedEvents = userActionRepository.findAllByUserId(userId).stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getWeight));
        List<RecommendedEvent> similarEvents = eventSimilarityRepository.findAllByEventAndEventIdInLimitedTo(
                        eventId, ratedEvents.keySet().stream().toList(), EVENT_COUNT_PREDICTION)
                .stream()
                .map(eventSimilarity -> genRecommendedEventFrom(eventSimilarity, eventId))
                .toList();

        double weightedSum = 0.0;
        double similaritySum = 0.0;

        for (RecommendedEvent event : similarEvents) {
            weightedSum += event.getScore() * ratedEvents.get(event.getEventId());
            similaritySum += event.getScore();
        }

        if (similaritySum != 0) {
            prediction = weightedSum / similaritySum;
        }

        return prediction;
    }
}
