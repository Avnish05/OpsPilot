package com.opspilot.incident.infrastructure.persistence;
import com.opspilot.incident.domain.*; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="alerts") class AlertEntity {
 @Id UUID id; @Column(name="event_id",nullable=false,unique=true) UUID eventId; @Column(name="service_id",nullable=false) UUID serviceId;
 @Enumerated(EnumType.STRING) @Column(nullable=false) AlertSource source; @Enumerated(EnumType.STRING) @Column(name="alert_type",nullable=false) AlertType alertType;
 @Enumerated(EnumType.STRING) @Column(nullable=false) Severity severity; @Column(nullable=false) String fingerprint; @Column(nullable=false) String title; @Column(nullable=false) String message;
 @Column(name="occurred_at",nullable=false) Instant occurredAt; @Column(name="created_at",nullable=false) Instant createdAt; @Version long version;
 protected AlertEntity(){} AlertEntity(UUID id,UUID eventId,UUID serviceId,AlertSource source,AlertType type,Severity severity,String fingerprint,String title,String message,Instant occurredAt,Instant createdAt){this.id=id;this.eventId=eventId;this.serviceId=serviceId;this.source=source;this.alertType=type;this.severity=severity;this.fingerprint=fingerprint;this.title=title;this.message=message;this.occurredAt=occurredAt;this.createdAt=createdAt;}
}
