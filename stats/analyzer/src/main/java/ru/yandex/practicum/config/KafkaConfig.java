package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

import java.util.Properties;

@Getter
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableConfigurationProperties({KafkaConfigProperties.class})
public class KafkaConfig {
    KafkaConfigProperties kafkaConfigProperties;

    public KafkaConfig(KafkaConfigProperties properties) {
        this.kafkaConfigProperties = properties;
    }

    @Bean
    public KafkaConsumer<Long, EventSimilarityAvro> getEventSimilarityConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.putAll(kafkaConfigProperties.getEventSimilarityConsumer().getProperties());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<Long, UserActionAvro> getUserActionConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.putAll(kafkaConfigProperties.getUserActionConsumer().getProperties());
        return new KafkaConsumer<>(props);
    }
}
