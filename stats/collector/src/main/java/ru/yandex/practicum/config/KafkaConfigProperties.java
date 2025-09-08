package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties(prefix = "collector.kafka")
public class KafkaConfigProperties {
    String bootstrapServers;
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
