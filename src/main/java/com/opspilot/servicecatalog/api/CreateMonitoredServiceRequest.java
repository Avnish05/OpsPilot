package com.opspilot.servicecatalog.api;

import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMonitoredServiceRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @NotNull ServiceEnvironment environment,
        @NotBlank @Size(max = 100) String ownerTeam
) { }
