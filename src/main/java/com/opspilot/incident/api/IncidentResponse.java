package com.opspilot.incident.api; import com.opspilot.incident.domain.*; import java.time.Instant; import java.util.UUID;
public record IncidentResponse(UUID id,UUID serviceId,String fingerprint,IncidentStatus status,Severity severity,String title,String resolutionSummary,Instant createdAt,Instant updatedAt){}
