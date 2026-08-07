package com.opspilot.deployment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opspilot.deployment.domain.DeploymentRepository;
import com.opspilot.deployment.domain.DeploymentStatus;
import com.opspilot.servicecatalog.application.MonitoredServiceQueryService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeploymentApplicationServiceTest {
    @Mock private DeploymentRepository deploymentRepository;
    @Mock private MonitoredServiceQueryService monitoredServiceQueryService;

    @Test
    void verifiesServiceExistsBeforeSavingDeployment() {
        Clock clock = Clock.fixed(Instant.parse("2026-08-07T00:00:00Z"), ZoneOffset.UTC);
        UUID serviceId = UUID.randomUUID();
        when(deploymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var applicationService = new DeploymentApplicationService(deploymentRepository, monitoredServiceQueryService, clock);

        var deployment = applicationService.create(serviceId, new CreateDeploymentCommand("2.4.1",
                DeploymentStatus.SUCCEEDED, Instant.parse("2026-08-06T23:55:00Z")));

        verify(monitoredServiceQueryService).get(serviceId);
        verify(deploymentRepository).save(any());
        assertThat(deployment.serviceId()).isEqualTo(serviceId);
        assertThat(deployment.createdAt()).isEqualTo(Instant.now(clock));
    }
}
