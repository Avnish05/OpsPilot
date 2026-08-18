package com.opspilot.alerting.application;

import com.opspilot.incident.api.ManualAlertRequest;
import com.opspilot.incident.domain.AlertSource;
import com.opspilot.incident.infrastructure.persistence.IncidentFacade;
import com.opspilot.shared.observability.OpsPilotMetrics;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertEventProcessor {
    private final ProcessedEventRepository processedEvents;
    private final IncidentFacade incidents;
    private final Validator validator;
    private final Clock clock;
    private final OpsPilotMetrics metrics;

    public AlertEventProcessor(ProcessedEventRepository processedEvents, IncidentFacade incidents, Validator validator, Clock clock, OpsPilotMetrics metrics) {
        this.processedEvents = processedEvents;
        this.incidents = incidents;
        this.validator = validator;
        this.clock = clock;
        this.metrics = metrics;
    }

    @Transactional
    public boolean process(AlertReceivedEvent event) {
        metrics.alertReceived();
        try {
            var violations = validator.validate(event);
            if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
            if (!"alert.received".equals(event.eventType()) || event.eventVersion() != 1) {
                throw new IllegalArgumentException("Unsupported alert event type or version");
            }
            if (!"SIMULATOR".equals(event.payload().source())) {
                throw new IllegalArgumentException("Unsupported alert source");
            }
            if (processedEvents.existsById(event.eventId())) {
                metrics.alertDuplicate();
                return false;
            }

            var payload = event.payload();
            incidents.createFromEvent(event.eventId(), AlertSource.SIMULATOR, new ManualAlertRequest(
                    payload.serviceId(), payload.alertType(), payload.severity(), payload.fingerprint(),
                    payload.title(), payload.message(), event.occurredAt()));
            processedEvents.save(new ProcessedEventEntity(event.eventId(), clock.instant()));
            return true;
        } catch (ConstraintViolationException | IllegalArgumentException exception) {
            metrics.alertRejected();
            throw exception;
        }
    }
}
