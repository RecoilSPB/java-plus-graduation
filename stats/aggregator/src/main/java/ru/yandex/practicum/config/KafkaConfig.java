package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Slf4j
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
    public KafkaProducer<Long, SpecificRecordBase> producer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigProperties.getBootstrapServers());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG,
                kafkaConfigProperties.getProducer().getClientId());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getProducer().getKeySerializer());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getProducer().getValueSerializer());
        log.info("properties for producer are: {}", properties);
        return new KafkaProducer<>(properties);
    }

    @Bean
    public KafkaConsumer<Long, UserActionAvro> consumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigProperties.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,
                kafkaConfigProperties.getConsumer().getGroupId());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG,
                kafkaConfigProperties.getConsumer().getClientId());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getConsumer().getKeyDeserializer());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getConsumer().getValueDeserializer());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaConfigProperties.getConsumer().getEnableAutoCommit());
        log.info("properties for consumer are: {}", properties);
        return new KafkaConsumer<>(properties);
    }
}
