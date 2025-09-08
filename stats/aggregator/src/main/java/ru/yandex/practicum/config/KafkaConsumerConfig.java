package ru.yandex.practicum.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka.consumer")
public class KafkaConsumerConfig {
    private ConsumerConfig consumer;

    @Getter
    @Setter
    public static class ConsumerConfig {
        private Long attemptTimeout;
        private Properties properties = new Properties();
        private Topic topic = new Topic();
    }

    @Getter
    @Setter
    public static class Topic {
        private String name;
    }
}
