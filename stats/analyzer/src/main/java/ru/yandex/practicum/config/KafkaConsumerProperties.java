package ru.yandex.practicum.config;

import lombok.*;

import java.util.Properties;

@Getter
@Setter
@NoArgsConstructor
public class KafkaConsumerProperties {
    Long attemptTimeout;
    Properties properties;
    Topic topic;

    @Getter
    @Setter
    public static class Topic {
        private String name;
    }
}
