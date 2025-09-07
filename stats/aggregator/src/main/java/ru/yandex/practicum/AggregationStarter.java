package ru.yandex.practicum;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.service.SimilarityService;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AggregationStarter {
    private final SimilarityService similarityService;
    private final Consumer<Long, UserActionAvro> consumer;
    private final Producer<Long, SpecificRecordBase> producer;
    private final KafkaConfig kafkaConfig;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaConfig.getKafkaConfigProperties().getUserActionTopic()));
            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer
                        .poll(Duration.ofMillis(kafkaConfig.getKafkaConfigProperties().getConsumer().getAttemptTimeout()));
                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    UserActionAvro userActionAvro = record.value();
                    log.trace("\nAggregationStarter: accepted {}", userActionAvro);
                    List<EventSimilarityAvro> similarities = similarityService.userActionHandle(userActionAvro);

                    for (EventSimilarityAvro eventSimilarity : similarities) {
                        log.trace("AggregationStarter: sending eventSimilarity {}", eventSimilarity);
                        producer.send(new ProducerRecord<>(kafkaConfig.getKafkaConfigProperties().getEventSimilarityTopic(),
                                null,
                                eventSimilarity));
                    }
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignores) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}