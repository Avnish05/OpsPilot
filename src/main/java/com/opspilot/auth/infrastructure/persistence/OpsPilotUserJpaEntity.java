package com.opspilot.auth.infrastructure.persistence;

import com.opspilot.auth.domain.UserRole;
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
@Table(name = "users")
class OpsPilotUserJpaEntity {
    @Id UUID id;
    @Column(nullable = false, length = 320) String email;
    @Column(name = "password_hash", nullable = false, length = 255) String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) UserRole role;
    @Column(nullable = false) boolean enabled;
    @Column(name = "created_at", nullable = false) Instant createdAt;
    @Column(name = "updated_at", nullable = false) Instant updatedAt;
    @Version @Column(nullable = false) long version;
    protected OpsPilotUserJpaEntity() { }
    OpsPilotUserJpaEntity(UUID id, String email, String passwordHash, UserRole role, boolean enabled, Instant createdAt,
                          Instant updatedAt, long version) {
        this.id=id; this.email=email; this.passwordHash=passwordHash; this.role=role; this.enabled=enabled;
        this.createdAt=createdAt; this.updatedAt=updatedAt; this.version=version;
    }
}
