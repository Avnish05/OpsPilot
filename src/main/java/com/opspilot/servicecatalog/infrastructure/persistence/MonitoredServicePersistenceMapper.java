package com.opspilot.servicecatalog.infrastructure.persistence;

import com.opspilot.servicecatalog.domain.MonitoredService;
import org.springframework.stereotype.Component;

@Component
class MonitoredServicePersistenceMapper {
    MonitoredServiceJpaEntity toEntity(MonitoredService service) {
        return new MonitoredServiceJpaEntity(service.id(), service.name(), service.description(), service.environment(),
                service.ownerTeam(), service.active(), service.createdAt(), service.updatedAt(), service.version());
    }

    MonitoredService toDomain(MonitoredServiceJpaEntity entity) {
        return MonitoredService.reconstitute(entity.id, entity.name, entity.description, entity.environment, entity.ownerTeam,
                entity.active, entity.createdAt, entity.updatedAt, entity.version);
    }
}
