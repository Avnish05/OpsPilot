package com.opspilot.deployment.api;

import com.opspilot.deployment.domain.DeploymentStatus;
import java.time.Instant;
import java.util.UUID;

public record DeploymentResponse(UUID id, UUID serviceId, String releaseVersion, DeploymentStatus status,
                                 Instant deployedAt, Instant createdAt, long version) { }
