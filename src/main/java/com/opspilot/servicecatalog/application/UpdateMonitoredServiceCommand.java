package com.opspilot.servicecatalog.application;

import com.opspilot.servicecatalog.domain.ServiceEnvironment;

public record UpdateMonitoredServiceCommand(String name, String description, ServiceEnvironment environment,
                                            String ownerTeam, Boolean active) {
}
