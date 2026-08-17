package com.opspilot.alerting.application;

import com.opspilot.incident.domain.AlertType;
import com.opspilot.incident.domain.Severity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AlertReceivedEvent(
        @NotNull UUID eventId,
        @NotBlank String eventType,
        @NotNull Integer eventVersion,
        @NotNull Instant occurredAt,
        @NotBlank String producer,
        @NotNull UUID correlationId,
        @NotNull @Valid Payload payload) {
    public record Payload(
            @NotNull UUID serviceId,
            @NotBlank String source,
            @NotNull AlertType alertType,
            @NotNull Severity severity,
            @NotBlank String fingerprint,
            @NotBlank String title,
            @NotBlank String message,
            Map<String, Object> attributes) { }
}
