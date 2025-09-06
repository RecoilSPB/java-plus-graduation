package ru.yandex.practicum.processor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

import ru.yandex.practicum.model.EventSimilarity;
import ru.yandex.practicum.repository.EventSimilarityRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventSimilarityProcessor implements Runnable {
    Consumer<Long, EventSimilarityAvro> consumer;
    KafkaConfig kafkaConfig;
    Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    EventSimilarityRepository eventSimilarityRepository;
    EventMapper eventMapper;

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(kafkaConfig.getKafkaConfigProperties().getEventSimilarityTopic()));
            while (true) {
                ConsumerRecords<Long, EventSimilarityAvro> records = consumer
                        .poll(Duration.ofMillis(kafkaConfig.getKafkaConfigProperties()
                                .getEventSimilarityConsumer().getAttemptTimeout()));
                int count = 0;
                for (ConsumerRecord<Long, EventSimilarityAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignores) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки события похожести ", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<Long, EventSimilarityAvro> consumerRecord) {
        log.info("handleRecord {}", consumerRecord);
        EventSimilarity eventSimilarity = eventMapper.mapToEventSimilarity(consumerRecord.value());

        eventSimilarityRepository.findByAeventIdAndBeventId(
                eventSimilarity.getAeventId(),
                eventSimilarity.getBeventId()).ifPresent(oldEventSimilarity ->
                eventSimilarity.setId(oldEventSimilarity.getId()));
        eventSimilarityRepository.save(eventSimilarity);
    }

    private void manageOffsets(ConsumerRecord<Long, EventSimilarityAvro> consumerRecord,
                               int count,
                               Consumer<Long, EventSimilarityAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                new OffsetAndMetadata(consumerRecord.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}