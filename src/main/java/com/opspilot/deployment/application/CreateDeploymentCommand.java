package com.opspilot.deployment.application;

import com.opspilot.deployment.domain.DeploymentStatus;
import java.time.Instant;

public record CreateDeploymentCommand(String releaseVersion, DeploymentStatus status, Instant deployedAt) {
}
