package com.opspilot.incident.api; import com.opspilot.incident.domain.*; import java.time.Instant; import java.util.UUID;
public record AlertResponse(UUID id,UUID eventId,UUID serviceId,AlertSource source,AlertType alertType,Severity severity,String fingerprint,String title,String message,Instant occurredAt,Instant createdAt){}
