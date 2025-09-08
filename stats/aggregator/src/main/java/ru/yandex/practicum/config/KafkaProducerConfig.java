package ru.yandex.practicum.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka.producer")
public class KafkaProducerConfig {
    private ProduceConfig producer;

    @Getter
    @Setter
    public static class ProduceConfig {
        private Properties properties = new Properties();
        private Topic topics = new Topic();
    }

    @Getter
    @Setter
    public static class Topic {
        private String name;
    }
}