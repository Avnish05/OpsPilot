package com.opspilot.incident.api;
import com.opspilot.incident.domain.*; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.UUID;
public record ManualAlertRequest(@NotNull UUID serviceId,@NotNull AlertType alertType,@NotNull Severity severity,@NotBlank @Size(max=255) String fingerprint,@NotBlank @Size(max=200) String title,@NotBlank @Size(max=2000) String message,@NotNull Instant occurredAt) {}
