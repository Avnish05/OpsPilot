package com.opspilot.alerting.application;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
class ProcessedEventEntity {
    @Id UUID eventId;
    Instant processedAt;
    protected ProcessedEventEntity() { }
    ProcessedEventEntity(UUID eventId, Instant processedAt) { this.eventId = eventId; this.processedAt = processedAt; }
}
