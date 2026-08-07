package com.opspilot.deployment.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.opspilot.deployment.domain.Deployment;
import com.opspilot.deployment.domain.DeploymentRepository;
import com.opspilot.deployment.domain.DeploymentStatus;
import com.opspilot.opspilot.TestcontainersConfiguration;
import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServiceRepository;
import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class JpaDeploymentRepositoryIntegrationTest {
    private final DeploymentRepository repository;
    private final MonitoredServiceRepository monitoredServiceRepository;

    @Autowired
    JpaDeploymentRepositoryIntegrationTest(DeploymentRepository repository, MonitoredServiceRepository monitoredServiceRepository) {
        this.repository = repository;
        this.monitoredServiceRepository = monitoredServiceRepository;
    }

    @Test
    void returnsServiceHistoryNewestDeploymentFirst() {
        UUID serviceId = UUID.randomUUID();
        Instant now = Instant.parse("2026-08-07T00:00:00Z");
        monitoredServiceRepository.save(MonitoredService.create(serviceId, "payments", null,
                ServiceEnvironment.PRODUCTION, "platform", now));
        Deployment oldDeployment = Deployment.create(UUID.randomUUID(), serviceId, "2.4.0", DeploymentStatus.SUCCEEDED,
                now.minusSeconds(120), now);
        Deployment latestDeployment = Deployment.create(UUID.randomUUID(), serviceId, "2.4.1", DeploymentStatus.STARTED,
                now.minusSeconds(60), now);
        repository.save(oldDeployment);
        repository.save(latestDeployment);

        var result = repository.findByServiceId(serviceId, 0, 20);

        assertThat(result.content()).extracting(Deployment::id)
                .containsExactly(latestDeployment.id(), oldDeployment.id());
    }
}
