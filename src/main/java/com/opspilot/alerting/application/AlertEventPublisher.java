package com.opspilot.alerting.application;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.opspilot.alerting.api.SimulatorAlertRequest;
import java.time.Clock;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertEventPublisher {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final String topic;

    public AlertEventPublisher(KafkaTemplate<String, String> kafka, ObjectMapper objectMapper, Clock clock,
            @Value("${opspilot.kafka.alerts-topic}") String topic) {
        this.kafka = kafka; this.objectMapper = objectMapper; this.clock = clock; this.topic = topic;
    }

    public UUID publish(SimulatorAlertRequest request) {
        UUID eventId = UUID.randomUUID();
        var event = new AlertReceivedEvent(eventId, "alert.received", 1, clock.instant(), "opspilot-simulator",
                UUID.randomUUID(), new AlertReceivedEvent.Payload(request.serviceId(), "SIMULATOR", request.alertType(),
                request.severity(), request.fingerprint(), request.title(), request.message(), request.attributes()));
        try {
            kafka.send(topic, request.serviceId().toString(), objectMapper.writeValueAsString(event));
            return eventId;
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not serialize alert event", exception);
        }
    }
}
