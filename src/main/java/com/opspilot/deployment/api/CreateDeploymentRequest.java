package com.opspilot.deployment.api;

import com.opspilot.deployment.domain.DeploymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateDeploymentRequest(
        @NotBlank @Size(max = 100) String releaseVersion,
        @NotNull DeploymentStatus status,
        @NotNull Instant deployedAt
) { }
