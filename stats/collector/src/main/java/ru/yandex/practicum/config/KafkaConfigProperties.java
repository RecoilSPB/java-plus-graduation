package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "collector.kafka")
public class KafkaConfigProperties {
    String bootstrapServers;
    String clientIdConfig;
    String producerKeySerializer;
    String producerValueSerializer;
    String userActionTopic;
}
