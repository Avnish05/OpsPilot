package com.opspilot.servicecatalog.domain;

import java.util.Optional;
import java.util.UUID;

public interface MonitoredServiceRepository {
    MonitoredService save(MonitoredService monitoredService);
    Optional<MonitoredService> findById(UUID id);
    boolean existsByNameAndEnvironment(String name, ServiceEnvironment environment, UUID excludedId);
    MonitoredServicePage findAll(int page, int size);
}
