package com.opspilot.deployment.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataDeploymentRepository extends JpaRepository<DeploymentJpaEntity, UUID> {
    Page<DeploymentJpaEntity> findByServiceId(UUID serviceId, Pageable pageable);
}
