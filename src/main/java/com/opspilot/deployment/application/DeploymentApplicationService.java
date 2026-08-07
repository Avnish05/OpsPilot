package com.opspilot.deployment.application;

import com.opspilot.deployment.domain.Deployment;
import com.opspilot.deployment.domain.DeploymentRepository;
import com.opspilot.servicecatalog.application.MonitoredServiceQueryService;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeploymentApplicationService {
    private final DeploymentRepository deploymentRepository;
    private final MonitoredServiceQueryService monitoredServiceQueryService;
    private final Clock clock;

    public DeploymentApplicationService(DeploymentRepository deploymentRepository,
                                        MonitoredServiceQueryService monitoredServiceQueryService, Clock clock) {
        this.deploymentRepository = deploymentRepository;
        this.monitoredServiceQueryService = monitoredServiceQueryService;
        this.clock = clock;
    }

    @Transactional
    public Deployment create(UUID serviceId, CreateDeploymentCommand command) {
        monitoredServiceQueryService.get(serviceId);
        return deploymentRepository.save(Deployment.create(UUID.randomUUID(), serviceId, command.releaseVersion(),
                command.status(), command.deployedAt(), Instant.now(clock)));
    }
}
