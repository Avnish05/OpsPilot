package com.opspilot.deployment.infrastructure.persistence;

import com.opspilot.deployment.domain.Deployment;
import org.springframework.stereotype.Component;

@Component
class DeploymentPersistenceMapper {
    DeploymentJpaEntity toEntity(Deployment deployment) {
        return new DeploymentJpaEntity(deployment.id(), deployment.serviceId(), deployment.releaseVersion(),
                deployment.status(), deployment.deployedAt(), deployment.createdAt(), deployment.version());
    }

    Deployment toDomain(DeploymentJpaEntity entity) {
        return Deployment.reconstitute(entity.id, entity.serviceId, entity.releaseVersion, entity.status,
                entity.deployedAt, entity.createdAt, entity.version);
    }
}
