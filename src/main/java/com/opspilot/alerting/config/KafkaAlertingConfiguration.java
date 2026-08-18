package com.opspilot.alerting.config;

import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaAlertingConfiguration {
    @Bean NewTopic alertsTopic(@Value("${opspilot.kafka.alerts-topic}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean NewTopic alertsDltTopic(@Value("${opspilot.kafka.alerts-dlt-topic}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean ConsumerFactory<String, String> alertConsumerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.consumer.group-id}") String groupId) {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                "bootstrap.servers", bootstrapServers,
                "group.id", groupId,
                "key.deserializer", StringDeserializer.class,
                "value.deserializer", StringDeserializer.class,
                "enable.auto.commit", false,
                "auto.offset.reset", "earliest"));
    }

    @Bean ProducerFactory<String, String> alertProducerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return new DefaultKafkaProducerFactory<>(Map.of(
                "bootstrap.servers", bootstrapServers,
                "key.serializer", StringSerializer.class,
                "value.serializer", StringSerializer.class));
    }

    @Bean KafkaTemplate<String, String> alertKafkaTemplate(ProducerFactory<String, String> alertProducerFactory) {
        return new KafkaTemplate<>(alertProducerFactory);
    }

    @Bean ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> alertConsumerFactory, KafkaTemplate<String, String> alertKafkaTemplate,
            @Value("${opspilot.kafka.alerts-dlt-topic}") String dltTopic) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(alertConsumerFactory);
        var recoverer = new DeadLetterPublishingRecoverer(alertKafkaTemplate,
                (record, exception) -> new org.apache.kafka.common.TopicPartition(dltTopic, record.partition()));
        factory.setCommonErrorHandler(new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L)));
        return factory;
    }
}
