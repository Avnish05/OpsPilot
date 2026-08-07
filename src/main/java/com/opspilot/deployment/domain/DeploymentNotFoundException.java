package com.opspilot.deployment.domain;

import java.util.UUID;

public final class DeploymentNotFoundException extends RuntimeException {
    public DeploymentNotFoundException(UUID id) {
        super("Deployment %s was not found".formatted(id));
    }
}
