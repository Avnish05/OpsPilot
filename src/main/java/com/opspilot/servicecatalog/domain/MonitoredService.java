package com.opspilot.servicecatalog.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class MonitoredService {

    private final UUID id;
    private String name;
    private String description;
    private ServiceEnvironment environment;
    private String ownerTeam;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;
    private final long version;

    private MonitoredService(UUID id, String name, String description, ServiceEnvironment environment,
                             String ownerTeam, boolean active, Instant createdAt, Instant updatedAt, long version) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = requiredText(name, "name");
        this.description = optionalText(description);
        this.environment = Objects.requireNonNull(environment, "environment must not be null");
        this.ownerTeam = requiredText(ownerTeam, "ownerTeam");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.version = version;
    }

    public static MonitoredService create(UUID id, String name, String description, ServiceEnvironment environment,
                                          String ownerTeam, Instant now) {
        return new MonitoredService(id, name, description, environment, ownerTeam, true, now, now, 0);
    }

    public static MonitoredService reconstitute(UUID id, String name, String description, ServiceEnvironment environment,
                                                String ownerTeam, boolean active, Instant createdAt, Instant updatedAt,
                                                long version) {
        return new MonitoredService(id, name, description, environment, ownerTeam, active, createdAt, updatedAt, version);
    }

    public void update(String name, String description, ServiceEnvironment environment, String ownerTeam,
                       Boolean active, Instant now) {
        if (name != null) this.name = requiredText(name, "name");
        if (description != null) this.description = optionalText(description);
        if (environment != null) this.environment = environment;
        if (ownerTeam != null) this.ownerTeam = requiredText(ownerTeam, "ownerTeam");
        if (active != null) this.active = active;
        this.updatedAt = Objects.requireNonNull(now, "now must not be null");
    }

    private static String requiredText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public ServiceEnvironment environment() { return environment; }
    public String ownerTeam() { return ownerTeam; }
    public boolean active() { return active; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
