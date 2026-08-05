package com.opspilot.servicecatalog.infrastructure.persistence;

import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataMonitoredServiceRepository extends JpaRepository<MonitoredServiceJpaEntity, UUID> {
    boolean existsByNameIgnoreCaseAndEnvironment(String name, ServiceEnvironment environment);
    boolean existsByNameIgnoreCaseAndEnvironmentAndIdNot(String name, ServiceEnvironment environment, UUID id);
    Page<MonitoredServiceJpaEntity> findAll(Pageable pageable);
}
