package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

import java.util.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SimilarityServiceImpl implements SimilarityService{

        Producer<Long, SpecificRecordBase> producer;
        KafkaConfig kafkaConfig;

        Map<Long, Map<Long, Double>> eventWeights = new HashMap<>();
        Map<Long, Double> eventSummaryWeights = new HashMap<>();
        Map<Long, Map<Long, Double>> eventMinSummaryWeights = new HashMap<>();

        @Override
        public List<EventSimilarityAvro> updateSimilarity(UserActionAvro userAction) {
            if (log.isDebugEnabled()) {
                log.debug("updateSimilarity for userAction = {}", userAction);
            }

            Long eventId = userAction.getEventId();
            Long userId = userAction.getUserId();
            double receivedWeight = getWeightByActionType(userAction.getActionType());
            double oldWeight = addOrUpdateEventWeightForUser(eventId, userId, receivedWeight);
            double newWeight = Math.max(oldWeight, receivedWeight);

            if (log.isDebugEnabled()) {
                log.debug("receivedWeight = {}, oldWeight = {}, newWeight = {}", receivedWeight, oldWeight, newWeight);
            }

            if (oldWeight == newWeight) {
                return Collections.emptyList();
            }

            if (log.isDebugEnabled()) {
                log.debug("starting update similarity");
            }

            // Обновляем суммарный вес
            double currentSummary = eventSummaryWeights.getOrDefault(eventId, 0.0);
            double updatedSummary = currentSummary + newWeight - oldWeight;
            eventSummaryWeights.put(eventId, updatedSummary);

            if (log.isDebugEnabled()) {
                log.debug("eventSummaryWeights updated: new summary weight for eventId = {} equals {}", eventId, updatedSummary);
            }

            // Корректно пересчитываем минимальные веса
            recalcEventMinSummaryWeights(eventId, userId, newWeight, oldWeight);

            List<EventSimilarityAvro> result = new ArrayList<>(eventWeights.size() - 1);
            double sqrtSumWeightA = Math.sqrt(updatedSummary);

            // Используем entrySet для избежания повторных вызовов get()
            for (Map.Entry<Long, Map<Long, Double>> entry : eventWeights.entrySet()) {
                Long secondEvent = entry.getKey();
                if (eventId.equals(secondEvent)) {
                    continue;
                }

                double sumWeightB = eventSummaryWeights.getOrDefault(secondEvent, 0.0);
                if (sumWeightB == 0) {
                    continue;
                }

                long eventA = Math.min(eventId, secondEvent);
                long eventB = Math.max(eventId, secondEvent);

                double sqrtSumWeightB = Math.sqrt(sumWeightB);
                double denominator = sqrtSumWeightA * sqrtSumWeightB;

                if (denominator > 0) {
                    double minSummaryWeight = getEventMinSummaryWeights(eventA, eventB);
                    double score = minSummaryWeight / denominator;

                    if (log.isTraceEnabled()) {
                        log.trace("eventA = {}, eventB = {}, sumWeightA = {}, sumWeightB = {}, score = {}",
                                eventA, eventB, updatedSummary, sumWeightB, score);
                    }

                    EventSimilarityAvro eventSimilarity = EventSimilarityAvro.newBuilder()
                            .setEventA(eventA)
                            .setEventB(eventB)
                            .setScore(score)
                            .setTimestamp(userAction.getTimestamp())
                            .build();

                    result.add(eventSimilarity);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Generated {} similarity records", result.size());
            }

            return result;
        }

        @Override
        public void collectEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
            ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(
                    kafkaConfig.getKafkaConfigProperties().getEventSimilarityTopic(),
                    null,
                    eventSimilarityAvro.getTimestamp().toEpochMilli(),
                    eventSimilarityAvro.getEventA(),
                    eventSimilarityAvro);
            producer.send(record);
        }

        @Override
        public void close() {
            if (producer != null) {
                producer.close();
            }
        }

        private void putEventMinSummaryWeights(long eventA, long eventB, double sum) {
            long first = Math.min(eventA, eventB);
            long second = Math.max(eventA, eventB);

            eventMinSummaryWeights
                    .computeIfAbsent(first, e -> new HashMap<>())
                    .put(second, sum);
        }

        private double getEventMinSummaryWeights(long eventA, long eventB) {
            long first = Math.min(eventA, eventB);
            long second = Math.max(eventA, eventB);

            Map<Long, Double> innerMap = eventMinSummaryWeights.get(first);
            return innerMap != null ? innerMap.getOrDefault(second, 0.0) : 0.0;
        }

        private double getWeightByActionType(ActionTypeAvro actionType) {
            return switch (actionType) {
                case VIEW -> 0.4;
                case REGISTER -> 0.8;
                case LIKE -> 1.0;
            };
        }

        private double addOrUpdateEventWeightForUser(Long eventId, Long userId, double weight) {
            Map<Long, Double> userWeights = eventWeights.computeIfAbsent(eventId, e -> new HashMap<>());
            double oldWeight = userWeights.getOrDefault(userId, 0.0);
            double maxWeight = Math.max(oldWeight, weight);

            if (oldWeight != maxWeight) {
                userWeights.put(userId, maxWeight);
                if (log.isDebugEnabled()) {
                    log.debug("eventWeights updated for eventId = {}: new weight = {}", eventId, maxWeight);
                }
            }

            return oldWeight;
        }

        private void recalcEventMinSummaryWeights(Long eventId, Long userId, double newWeight, double oldWeight) {
            double weightDiff = newWeight - oldWeight;

            for (Map.Entry<Long, Map<Long, Double>> entry : eventWeights.entrySet()) {
                Long otherEvent = entry.getKey();
                if (eventId.equals(otherEvent)) {
                    continue;
                }

                Map<Long, Double> otherUserWeights = entry.getValue();
                double otherWeight = otherUserWeights.getOrDefault(userId, 0.0);

                // Текущее минимальное значение для этой пары событий
                double currentMin = getEventMinSummaryWeights(eventId, otherEvent);

                // Новое минимальное значение - минимум из нового веса и веса другого события
                double potentialNewMin = Math.min(newWeight, otherWeight);

                // Если старое минимальное значение было равно oldWeight (который был меньше newWeight)
                // и oldWeight был меньше otherWeight, то нам нужно обновить минимальное значение
                if (currentMin == oldWeight && oldWeight <= otherWeight) {
                    putEventMinSummaryWeights(eventId, otherEvent, potentialNewMin);
                }
                // Если оба веса увеличиваются, минимальное значение может увеличиться
                else if (potentialNewMin > currentMin) {
                    putEventMinSummaryWeights(eventId, otherEvent, potentialNewMin);
                }
            }
        }
    }