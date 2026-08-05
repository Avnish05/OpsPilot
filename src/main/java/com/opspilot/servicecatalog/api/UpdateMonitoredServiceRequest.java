package com.opspilot.servicecatalog.api;

import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import jakarta.validation.constraints.Size;

public record UpdateMonitoredServiceRequest(
        @Size(min = 1, max = 100) String name,
        @Size(max = 500) String description,
        ServiceEnvironment environment,
        @Size(min = 1, max = 100) String ownerTeam,
        Boolean active
) { }
