package com.opspilot.servicecatalog.api;

import com.opspilot.servicecatalog.application.CreateMonitoredServiceCommand;
import com.opspilot.servicecatalog.application.UpdateMonitoredServiceCommand;
import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServicePage;
import org.springframework.stereotype.Component;

@Component
class MonitoredServiceApiMapper {
    CreateMonitoredServiceCommand toCommand(CreateMonitoredServiceRequest request) {
        return new CreateMonitoredServiceCommand(request.name(), request.description(), request.environment(), request.ownerTeam());
    }

    UpdateMonitoredServiceCommand toCommand(UpdateMonitoredServiceRequest request) {
        return new UpdateMonitoredServiceCommand(request.name(), request.description(), request.environment(), request.ownerTeam(), request.active());
    }

    MonitoredServiceResponse toResponse(MonitoredService service) {
        return new MonitoredServiceResponse(service.id(), service.name(), service.description(), service.environment(),
                service.ownerTeam(), service.active(), service.createdAt(), service.updatedAt(), service.version());
    }

    MonitoredServicePageResponse toResponse(MonitoredServicePage page) {
        return new MonitoredServicePageResponse(page.content().stream().map(this::toResponse).toList(), page.page(), page.size(),
                page.totalElements(), page.totalPages());
    }
}
