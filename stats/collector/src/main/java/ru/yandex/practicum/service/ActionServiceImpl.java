package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.mapper.UserActionMapper;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService{

    private final Producer<String, SpecificRecordBase> producer;
    private final KafkaConfig kafkaConfig;
    private final UserActionMapper userActionMapper;

    @Override
    public void collectUserAction(UserAction userAction) {
        log.info("ActionService: call collectUserAction for UserAction = {}", userAction);
        String userActionTopic = kafkaConfig.getKafkaConfigProperties().getUserActionTopic();
        UserActionAvro userActionAvro = userActionMapper.toUserActionAvro(userAction);
        long timestamp = userAction.getTimestamp().toEpochMilli();
        Long EventId = userAction.getEventId();
        send(userActionTopic,
                EventId,
                timestamp,
                userActionAvro);
    }

    private void send(String topic, Long key, Long timestamp, SpecificRecordBase specificRecordBase) {
        ProducerRecord<String, SpecificRecordBase> rec = new ProducerRecord<>(
                topic,
                null,
                timestamp,
                key.toString(),
                specificRecordBase);
        producer.send(rec, (metadata, exception) -> {
            if (exception != null) {
                log.error("Kafka: сообщение НЕ ОТПРАВЛЕНО, topic: {}", topic, exception);
            } else {
                log.info("Kafka: сообщение УСПЕШНО отправлено, topic: {}, partition: {}, offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
