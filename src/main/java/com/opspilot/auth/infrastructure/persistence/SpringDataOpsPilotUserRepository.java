package com.opspilot.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataOpsPilotUserRepository extends JpaRepository<OpsPilotUserJpaEntity, UUID> {
    Optional<OpsPilotUserJpaEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
