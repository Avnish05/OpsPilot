package com.opspilot.servicecatalog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServiceRepository;
import com.opspilot.servicecatalog.domain.ServiceAlreadyExistsException;
import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MonitoredServiceApplicationServiceTest {
    @Mock
    private MonitoredServiceRepository repository;

    private final Clock clock = Clock.fixed(Instant.parse("2026-08-05T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void createsServiceWhenNameIsAvailable() {
        when(repository.existsByNameAndEnvironment("payments", ServiceEnvironment.PRODUCTION, null)).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var service = new MonitoredServiceApplicationService(repository, clock).create(
                new CreateMonitoredServiceCommand("payments", null, ServiceEnvironment.PRODUCTION, "platform"));

        assertThat(service.name()).isEqualTo("payments");
        assertThat(service.createdAt()).isEqualTo(Instant.now(clock));
    }

    @Test
    void rejectsDuplicateDuringCreate() {
        when(repository.existsByNameAndEnvironment("payments", ServiceEnvironment.PRODUCTION, null)).thenReturn(true);
        var applicationService = new MonitoredServiceApplicationService(repository, clock);

        assertThatThrownBy(() -> applicationService.create(
                new CreateMonitoredServiceCommand("payments", null, ServiceEnvironment.PRODUCTION, "platform")))
                .isInstanceOf(ServiceAlreadyExistsException.class);
    }

    @Test
    void rejectsDuplicateDuringUpdateExcludingCurrentService() {
        UUID id = UUID.randomUUID();
        MonitoredService existing = MonitoredService.create(id, "payments", null, ServiceEnvironment.STAGING, "platform", Instant.now(clock));
        when(repository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(repository.existsByNameAndEnvironment(eq("payments"), eq(ServiceEnvironment.PRODUCTION), eq(id))).thenReturn(true);
        var applicationService = new MonitoredServiceApplicationService(repository, clock);

        assertThatThrownBy(() -> applicationService.update(id,
                new UpdateMonitoredServiceCommand(null, null, ServiceEnvironment.PRODUCTION, null, null)))
                .isInstanceOf(ServiceAlreadyExistsException.class);
    }
}
