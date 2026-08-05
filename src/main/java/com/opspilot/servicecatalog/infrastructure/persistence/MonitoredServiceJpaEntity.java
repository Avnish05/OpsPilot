package com.opspilot.servicecatalog.infrastructure.persistence;

import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "monitored_services")
class MonitoredServiceJpaEntity {
    @Id
    UUID id;
    @Column(nullable = false, length = 100)
    String name;
    @Column(length = 500)
    String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    ServiceEnvironment environment;
    @Column(name = "owner_team", nullable = false, length = 100)
    String ownerTeam;
    @Column(nullable = false)
    boolean active;
    @Column(name = "created_at", nullable = false)
    Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;
    @Version
    @Column(nullable = false)
    long version;

    protected MonitoredServiceJpaEntity() { }

    MonitoredServiceJpaEntity(UUID id, String name, String description, ServiceEnvironment environment, String ownerTeam,
                              boolean active, Instant createdAt, Instant updatedAt, long version) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.environment = environment;
        this.ownerTeam = ownerTeam;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
