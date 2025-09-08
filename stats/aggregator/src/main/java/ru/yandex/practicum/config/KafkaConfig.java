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
@EnableConfigurationProperties({KafkaCommonConfig.class, KafkaProducerConfig.class, KafkaConsumerConfig.class})
public class KafkaConfig {
    KafkaCommonConfig kafkaCommonConfig;
    KafkaProducerConfig kafkaProducerConfig;
    KafkaConsumerConfig kafkaConsumerConfig;

    public KafkaConfig(KafkaCommonConfig kafkaCommonConfig,
                       KafkaProducerConfig kafkaProducerConfig,
                       KafkaConsumerConfig kafkaConsumerConfig) {
        this.kafkaCommonConfig = kafkaCommonConfig;
        this.kafkaProducerConfig = kafkaProducerConfig;
        this.kafkaConsumerConfig = kafkaConsumerConfig;
    }

    @Bean
    public KafkaProducer<Long, SpecificRecordBase> producer() {
        Properties properties = new Properties();
        properties.putAll(kafkaProducerConfig.getProperties());
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCommonConfig.getBootstrapServers());
        log.info("properties for producer are: {}", properties);
        return new KafkaProducer<>(properties);
    }

    @Bean
    public KafkaConsumer<Long, UserActionAvro> consumer() {
        Properties properties = new Properties();
        properties.putAll(kafkaConsumerConfig.getProperties());
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCommonConfig.getBootstrapServers());
        log.info("properties for consumer are: {}", properties);
        return new KafkaConsumer<>(properties);
    }
}
