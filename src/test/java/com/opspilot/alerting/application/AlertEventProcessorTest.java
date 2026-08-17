package com.opspilot.alerting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.opspilot.incident.domain.AlertType;
import com.opspilot.incident.domain.Severity;
import com.opspilot.incident.infrastructure.persistence.IncidentFacade;
import jakarta.validation.Validation;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AlertEventProcessorTest {
    private final ProcessedEventRepository processedEvents = mock(ProcessedEventRepository.class);
    private final IncidentFacade incidents = mock(IncidentFacade.class);
    private final AlertEventProcessor processor = new AlertEventProcessor(processedEvents, incidents,
            Validation.buildDefaultValidatorFactory().getValidator(), Clock.fixed(Instant.parse("2026-08-12T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void processesAValidEventOnce() {
        var event = event();

        assertThat(processor.process(event)).isTrue();

        verify(incidents).createFromEvent(eq(event.eventId()), any(), any());
        verify(processedEvents).save(any());
    }

    @Test
    void ignoresAnAlreadyProcessedEvent() {
        var event = event();
        when(processedEvents.existsById(event.eventId())).thenReturn(true);

        assertThat(processor.process(event)).isFalse();

        verifyNoInteractions(incidents);
        verify(processedEvents, never()).save(any());
    }

    @Test
    void rejectsAnUnsupportedEventVersion() {
        var valid = event();
        var unsupported = new AlertReceivedEvent(valid.eventId(), valid.eventType(), 2, valid.occurredAt(), valid.producer(),
                valid.correlationId(), valid.payload());

        assertThatThrownBy(() -> processor.process(unsupported)).isInstanceOf(IllegalArgumentException.class);
    }

    private AlertReceivedEvent event() {
        return new AlertReceivedEvent(UUID.randomUUID(), "alert.received", 1, Instant.parse("2026-08-12T00:00:00Z"),
                "opspilot-simulator", UUID.randomUUID(), new AlertReceivedEvent.Payload(UUID.randomUUID(), "SIMULATOR",
                AlertType.HIGH_ERROR_RATE, Severity.HIGH, "checkout:http-500", "HTTP 500 rate increased",
                "The threshold was exceeded", Map.of("errorRate", 0.24)));
    }
}
