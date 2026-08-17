package com.opspilot.alerting.api;

import com.opspilot.alerting.application.AlertEventPublisher;
import com.opspilot.incident.domain.AlertType;
import com.opspilot.incident.domain.Severity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@Validated
@RestController
@RequestMapping("/api/v1/simulator")
public class SimulatorController {
    private final AlertEventPublisher publisher;
    public SimulatorController(AlertEventPublisher publisher) { this.publisher = publisher; }

    @PostMapping("/alerts")
    public ResponseEntity<Map<String, UUID>> alert(@Valid @RequestBody SimulatorAlertRequest request) { return accepted(publisher.publish(request)); }

    @PostMapping("/scenarios/deployment-regression")
    public ResponseEntity<Map<String, UUID>> deploymentRegression(@RequestParam @NotNull UUID serviceId) {
        return accepted(publisher.publish(new SimulatorAlertRequest(serviceId, AlertType.HIGH_ERROR_RATE, Severity.HIGH,
                serviceId + ":deployment-regression", "Error rate increased after deployment", "HTTP 500 responses exceeded the threshold", Map.of("errorRate", 0.24))));
    }

    @PostMapping("/scenarios/database-outage")
    public ResponseEntity<Map<String, UUID>> databaseOutage(@RequestParam @NotNull UUID serviceId) {
        return accepted(publisher.publish(new SimulatorAlertRequest(serviceId, AlertType.DATABASE_TIMEOUT, Severity.CRITICAL,
                serviceId + ":database-timeout", "Database timeouts detected", "Database requests are timing out", Map.of())));
    }

    @PostMapping("/scenarios/kafka-lag")
    public ResponseEntity<Map<String, UUID>> kafkaLag(@RequestParam @NotNull UUID serviceId) {
        return accepted(publisher.publish(new SimulatorAlertRequest(serviceId, AlertType.KAFKA_CONSUMER_LAG, Severity.WARNING,
                serviceId + ":kafka-consumer-lag", "Kafka consumer lag increased", "Consumer lag exceeded the threshold", Map.of())));
    }

    private ResponseEntity<Map<String, UUID>> accepted(UUID eventId) {
        return ResponseEntity.accepted().location(URI.create("/api/v1/alerts?eventId=" + eventId)).body(Map.of("eventId", eventId));
    }
}
