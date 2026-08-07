package com.opspilot.deployment.domain;

import java.util.Optional;
import java.util.UUID;

public interface DeploymentRepository {
    Deployment save(Deployment deployment);
    Optional<Deployment> findById(UUID id);
    DeploymentPage findByServiceId(UUID serviceId, int page, int size);
}
