package com.opspilot.servicecatalog.domain;

import java.util.UUID;

public final class MonitoredServiceNotFoundException extends RuntimeException {
    public MonitoredServiceNotFoundException(UUID id) {
        super("Monitored service %s was not found".formatted(id));
    }
}
