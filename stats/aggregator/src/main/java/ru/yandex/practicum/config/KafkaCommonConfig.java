package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author PopovN
 * @created 08.09.2025 16:42
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaCommonConfig {
    private String bootstrapServers;
}
