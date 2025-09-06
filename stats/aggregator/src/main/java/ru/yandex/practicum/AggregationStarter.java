package ru.yandex.practicum;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.service.SimilarityService;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AggregationStarter {
    static final int BATCH_COMMIT_THRESHOLD = 10;
    static final int MAX_POLL_RECORDS = 500;

    final SimilarityService similarityService;
    final KafkaConsumer<Long, UserActionAvro> consumer;
    final KafkaConfig kafkaConfig;
    final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    final AtomicInteger processedRecordsCount = new AtomicInteger(0);
    volatile boolean running = true;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        try {
            String topic = kafkaConfig.getKafkaConfigProperties().getUserActionTopic();
            consumer.subscribe(Collections.singletonList(topic));
            log.info("Subscribed to topic: {}", topic);

            while (running) {
                processRecordsBatch();
            }

        } catch (WakeupException e) {
            log.info("Consumer wakeup requested");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            shutdownResources();
        }
    }

    private void processRecordsBatch() {
        Duration timeout = Duration.ofMillis(kafkaConfig.getKafkaConfigProperties().getConsumer().getAttemptTimeout());
        ConsumerRecords<Long, UserActionAvro> records = consumer.poll(timeout);

        if (records.isEmpty()) {
            return;
        }

        log.debug("Polled {} records from Kafka", records.count());

        for (ConsumerRecord<Long, UserActionAvro> record : records) {
            try {
                processSingleRecord(record);
            } catch (Exception e) {
                log.error("Ошибка обработки записи: topic={}, partition={}, offset={}",
                        record.topic(), record.partition(), record.offset(), e);
                // Продолжаем обработку следующих записей, не прерывая весь батч
            }
        }

        commitOffsetsAsync();
    }

    private void processSingleRecord(ConsumerRecord<Long, UserActionAvro> record) {
        if (log.isDebugEnabled()) {
            log.debug("Processing record: key={}, offset={}, partition={}",
                    record.key(), record.offset(), record.partition());
        }

        handleRecord(record);
        updateOffset(record);

        if (processedRecordsCount.incrementAndGet() % BATCH_COMMIT_THRESHOLD == 0) {
            commitOffsetsAsyncWithCallback();
        }
    }

    private void handleRecord(ConsumerRecord<Long, UserActionAvro> consumerRecord) {
        try {
            List<EventSimilarityAvro> eventSimilarityList = similarityService.updateSimilarity(consumerRecord.value());
            for (EventSimilarityAvro eventSimilarity : eventSimilarityList) {
                similarityService.collectEventSimilarity(eventSimilarity);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки UserActionAvro: {}", consumerRecord.value(), e);
            throw e; // Перебрасываем для единообразной обработки
        }
    }

    private void updateOffset(ConsumerRecord<Long, UserActionAvro> consumerRecord) {
        TopicPartition partition = new TopicPartition(consumerRecord.topic(), consumerRecord.partition());
        currentOffsets.put(partition, new OffsetAndMetadata(consumerRecord.offset() + 1));
    }

    private void commitOffsetsAsync() {
        if (!currentOffsets.isEmpty()) {
            consumer.commitAsync(currentOffsets, null);
        }
    }

    private void commitOffsetsAsyncWithCallback() {
        if (!currentOffsets.isEmpty()) {
            consumer.commitAsync(currentOffsets, this::handleCommitCallback);
        }
    }

    private void handleCommitCallback(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
        if (exception != null) {
            log.warn("Ошибка во время фиксации оффсетов", exception);
        } else if (log.isDebugEnabled()) {
            log.debug("Успешно закоммичены оффсеты: {}", offsets);
        }
    }

    private void shutdown() {
        running = false;
        consumer.wakeup();
    }

    private void shutdownResources() {
        try {
            log.info("Финализация оффсетов...");
            consumer.commitSync(currentOffsets);
        } catch (Exception e) {
            log.warn("Ошибка при финальном коммите оффсетов", e);
        } finally {
            closeConsumer();
            closeProducer();
        }
    }

    private void closeConsumer() {
        try {
            log.info("Закрываем консьюмер");
            consumer.close();
        } catch (Exception e) {
            log.warn("Ошибка при закрытии консьюмера", e);
        }
    }

    private void closeProducer() {
        try {
            log.info("Закрываем продюсер");
            similarityService.close();
        } catch (Exception e) {
            log.warn("Ошибка при закрытии продюсера", e);
        }
    }
}