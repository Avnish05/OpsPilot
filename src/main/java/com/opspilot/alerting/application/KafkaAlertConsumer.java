package com.opspilot.alerting.application;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaAlertConsumer {
    private final ObjectMapper objectMapper;
    private final AlertEventProcessor processor;

    public KafkaAlertConsumer(ObjectMapper objectMapper, AlertEventProcessor processor) {
        this.objectMapper = objectMapper;
        this.processor = processor;
    }

    @KafkaListener(topics = "${opspilot.kafka.alerts-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void receive(String rawEvent) {
        try {
            processor.process(objectMapper.readValue(rawEvent, AlertReceivedEvent.class));
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Alert event is not valid JSON", exception);
        }
    }
}
