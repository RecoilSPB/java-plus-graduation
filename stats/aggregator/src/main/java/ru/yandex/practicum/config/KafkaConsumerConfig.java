package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka.consumer")
public class KafkaConsumerConfig {
    private Long attemptTimeout;
    private Properties properties;
    private Topic topic;

    @Getter
    @Setter
    public static class Topic {
        private String name;
    }
}
