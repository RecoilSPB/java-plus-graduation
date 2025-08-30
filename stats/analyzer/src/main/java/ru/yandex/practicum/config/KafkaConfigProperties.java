package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "analyzer.kafka")
public class KafkaConfigProperties {
    String bootstrapServers;
    KafkaProperties userActionConsumer;
    KafkaProperties eventSimilarityConsumer;

    String userActionTopic;
    String eventSimilarityTopic;
}
