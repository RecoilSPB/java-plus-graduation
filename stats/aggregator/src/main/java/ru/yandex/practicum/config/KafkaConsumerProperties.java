package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class KafkaConsumerProperties {
    String groupId;
    String clientId;
    String keyDeserializer;
    String valueDeserializer;
    Long attemptTimeout;
    String enableAutoCommit;
}
