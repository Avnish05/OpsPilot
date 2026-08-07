package com.opspilot.deployment.infrastructure.persistence;

import com.opspilot.deployment.domain.DeploymentStatus;
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
@Table(name = "deployments")
class DeploymentJpaEntity {
    @Id UUID id;
    @Column(name = "service_id", nullable = false) UUID serviceId;
    @Column(name = "release_version", nullable = false, length = 100) String releaseVersion;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) DeploymentStatus status;
    @Column(name = "deployed_at", nullable = false) Instant deployedAt;
    @Column(name = "created_at", nullable = false) Instant createdAt;
    @Version @Column(nullable = false) long version;

    protected DeploymentJpaEntity() { }

    DeploymentJpaEntity(UUID id, UUID serviceId, String releaseVersion, DeploymentStatus status, Instant deployedAt,
                        Instant createdAt, long version) {
        this.id = id;
        this.serviceId = serviceId;
        this.releaseVersion = releaseVersion;
        this.status = status;
        this.deployedAt = deployedAt;
        this.createdAt = createdAt;
        this.version = version;
    }
}
