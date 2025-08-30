package ru.yandex.practicum.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KafkaProperties {
    private String groupId;
    private String clientId;
    private String keyDeserializer;
    private String valueDeserializer;
    private long attemptTimeout;
    private String enableAutoCommit;
}
