package com.opspilot.deployment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Deployment {
    private final UUID id;
    private final UUID serviceId;
    private final String releaseVersion;
    private final DeploymentStatus status;
    private final Instant deployedAt;
    private final Instant createdAt;
    private final long version;

    private Deployment(UUID id, UUID serviceId, String releaseVersion, DeploymentStatus status, Instant deployedAt,
                       Instant createdAt, long version) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.serviceId = Objects.requireNonNull(serviceId, "serviceId must not be null");
        if (releaseVersion == null || releaseVersion.isBlank()) {
            throw new IllegalArgumentException("releaseVersion must not be blank");
        }
        this.releaseVersion = releaseVersion.trim();
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.deployedAt = Objects.requireNonNull(deployedAt, "deployedAt must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.version = version;
    }

    public static Deployment create(UUID id, UUID serviceId, String releaseVersion, DeploymentStatus status,
                                    Instant deployedAt, Instant now) {
        return new Deployment(id, serviceId, releaseVersion, status, deployedAt, now, 0);
    }

    public static Deployment reconstitute(UUID id, UUID serviceId, String releaseVersion, DeploymentStatus status,
                                          Instant deployedAt, Instant createdAt, long version) {
        return new Deployment(id, serviceId, releaseVersion, status, deployedAt, createdAt, version);
    }

    public UUID id() { return id; }
    public UUID serviceId() { return serviceId; }
    public String releaseVersion() { return releaseVersion; }
    public DeploymentStatus status() { return status; }
    public Instant deployedAt() { return deployedAt; }
    public Instant createdAt() { return createdAt; }
    public long version() { return version; }
}
