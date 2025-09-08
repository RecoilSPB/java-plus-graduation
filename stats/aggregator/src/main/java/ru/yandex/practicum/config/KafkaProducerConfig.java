package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka.producer")
public class KafkaProducerConfig {
    private Properties properties = new Properties();
    private Topic topic = new Topic();


    @Getter
    @Setter
    public static class Topic {
        private String name;
    }
}