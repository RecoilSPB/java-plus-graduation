package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.stats.avro.UserActionAvro;

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
    public KafkaConsumer<String, EventSimilarityAvro> getEventSimilarityConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigProperties.getEventSimilarityConsumer().getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConfigProperties.getEventSimilarityConsumer().getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getEventSimilarityConsumer().getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getEventSimilarityConsumer().getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaConfigProperties.getEventSimilarityConsumer().getEnableAutoCommit());

        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, UserActionAvro> getUserActionConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigProperties.getUserActionConsumer().getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConfigProperties.getUserActionConsumer().getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getUserActionConsumer().getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getUserActionConsumer().getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaConfigProperties.getUserActionConsumer().getEnableAutoCommit());
        return new KafkaConsumer<>(props);
    }
}
