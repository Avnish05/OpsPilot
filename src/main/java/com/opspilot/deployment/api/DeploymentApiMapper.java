package com.opspilot.deployment.api;

import com.opspilot.deployment.application.CreateDeploymentCommand;
import com.opspilot.deployment.domain.Deployment;
import com.opspilot.deployment.domain.DeploymentPage;
import org.springframework.stereotype.Component;

@Component
class DeploymentApiMapper {
    CreateDeploymentCommand toCommand(CreateDeploymentRequest request) {
        return new CreateDeploymentCommand(request.releaseVersion(), request.status(), request.deployedAt());
    }

    DeploymentResponse toResponse(Deployment deployment) {
        return new DeploymentResponse(deployment.id(), deployment.serviceId(), deployment.releaseVersion(),
                deployment.status(), deployment.deployedAt(), deployment.createdAt(), deployment.version());
    }

    DeploymentPageResponse toResponse(DeploymentPage page) {
        return new DeploymentPageResponse(page.content().stream().map(this::toResponse).toList(), page.page(), page.size(),
                page.totalElements(), page.totalPages());
    }
}
