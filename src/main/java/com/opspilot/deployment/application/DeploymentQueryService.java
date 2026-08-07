package com.opspilot.deployment.application;

import com.opspilot.deployment.domain.Deployment;
import com.opspilot.deployment.domain.DeploymentNotFoundException;
import com.opspilot.deployment.domain.DeploymentPage;
import com.opspilot.deployment.domain.DeploymentRepository;
import com.opspilot.servicecatalog.application.MonitoredServiceQueryService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeploymentQueryService {
    private final DeploymentRepository deploymentRepository;
    private final MonitoredServiceQueryService monitoredServiceQueryService;

    public DeploymentQueryService(DeploymentRepository deploymentRepository,
                                  MonitoredServiceQueryService monitoredServiceQueryService) {
        this.deploymentRepository = deploymentRepository;
        this.monitoredServiceQueryService = monitoredServiceQueryService;
    }

    @Transactional(readOnly = true)
    public Deployment get(UUID deploymentId) {
        return deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new DeploymentNotFoundException(deploymentId));
    }

    @Transactional(readOnly = true)
    public DeploymentPage listForService(UUID serviceId, int page, int size) {
        monitoredServiceQueryService.get(serviceId);
        return deploymentRepository.findByServiceId(serviceId, page, size);
    }
}
