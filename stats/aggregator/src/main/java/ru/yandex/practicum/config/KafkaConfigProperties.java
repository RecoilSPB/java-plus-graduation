package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaConfigProperties {
    String bootstrapServers;
    KafkaConsumerProperties consumer;
    KafkaProducerProperties producer;

    String userActionTopic;
    String eventSimilarityTopic;
}
