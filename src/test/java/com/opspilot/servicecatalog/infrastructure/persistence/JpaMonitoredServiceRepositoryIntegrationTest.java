package com.opspilot.servicecatalog.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.opspilot.opspilot.TestcontainersConfiguration;
import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServiceRepository;
import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class JpaMonitoredServiceRepositoryIntegrationTest {
    private final MonitoredServiceRepository repository;

    @Autowired
    JpaMonitoredServiceRepositoryIntegrationTest(MonitoredServiceRepository repository) {
        this.repository = repository;
    }

    @Test
    void persistsServicesAndEnforcesCaseInsensitiveDuplicateLookup() {
        MonitoredService service = MonitoredService.create(UUID.randomUUID(), "Payments", null,
                ServiceEnvironment.PRODUCTION, "platform", Instant.parse("2026-08-05T00:00:00Z"));
        repository.save(service);

        assertThat(repository.findById(service.id())).isPresent();
        assertThat(repository.existsByNameAndEnvironment("payments", ServiceEnvironment.PRODUCTION, null)).isTrue();
        assertThat(repository.existsByNameAndEnvironment("payments", ServiceEnvironment.STAGING, null)).isFalse();
        assertThat(repository.existsByNameAndEnvironment("payments", ServiceEnvironment.PRODUCTION, service.id())).isFalse();
    }
}
