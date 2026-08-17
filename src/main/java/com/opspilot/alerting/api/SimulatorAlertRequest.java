package com.opspilot.alerting.api;

import com.opspilot.incident.domain.AlertType;
import com.opspilot.incident.domain.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

public record SimulatorAlertRequest(
        @NotNull UUID serviceId,
        @NotNull AlertType alertType,
        @NotNull Severity severity,
        @NotBlank @Size(max = 255) String fingerprint,
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 2000) String message,
        Map<String, Object> attributes) { }
