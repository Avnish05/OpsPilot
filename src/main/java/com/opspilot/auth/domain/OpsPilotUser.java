package com.opspilot.auth.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class OpsPilotUser {
    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final boolean enabled;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    private OpsPilotUser(UUID id, String email, String passwordHash, UserRole role, boolean enabled,
                         Instant createdAt, Instant updatedAt, long version) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = normalizeEmail(email);
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.enabled = enabled;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.version = version;
    }

    public static OpsPilotUser register(UUID id, String email, String passwordHash, Instant now) {
        return new OpsPilotUser(id, email, passwordHash, UserRole.ENGINEER, true, now, now, 0);
    }

    public static OpsPilotUser reconstitute(UUID id, String email, String passwordHash, UserRole role, boolean enabled,
                                            Instant createdAt, Instant updatedAt, long version) {
        return new OpsPilotUser(id, email, passwordHash, role, enabled, createdAt, updatedAt, version);
    }

    private static String normalizeEmail(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("email must not be blank");
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public UUID id() { return id; }
    public String email() { return email; }
    public String passwordHash() { return passwordHash; }
    public UserRole role() { return role; }
    public boolean enabled() { return enabled; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
