package com.opspilot.servicecatalog.api;

import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import java.time.Instant;
import java.util.UUID;

public record MonitoredServiceResponse(UUID id, String name, String description, ServiceEnvironment environment,
                                       String ownerTeam, boolean active, Instant createdAt, Instant updatedAt, long version) { }
