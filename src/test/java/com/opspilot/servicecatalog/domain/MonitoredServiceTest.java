package com.opspilot.servicecatalog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MonitoredServiceTest {
    @Test
    void createsActiveServiceAndTrimsText() {
        Instant now = Instant.parse("2026-08-05T00:00:00Z");
        MonitoredService service = MonitoredService.create(UUID.randomUUID(), " payments ", " description ",
                ServiceEnvironment.PRODUCTION, " platform ", now);

        assertThat(service.name()).isEqualTo("payments");
        assertThat(service.description()).isEqualTo("description");
        assertThat(service.ownerTeam()).isEqualTo("platform");
        assertThat(service.active()).isTrue();
        assertThat(service.createdAt()).isEqualTo(now);
        assertThat(service.updatedAt()).isEqualTo(now);
    }

    @Test
    void updatesOnlyProvidedFields() {
        Instant createdAt = Instant.parse("2026-08-05T00:00:00Z");
        MonitoredService service = MonitoredService.create(UUID.randomUUID(), "payments", "old", ServiceEnvironment.STAGING,
                "platform", createdAt);
        Instant updatedAt = Instant.parse("2026-08-05T01:00:00Z");

        service.update(null, "", ServiceEnvironment.PRODUCTION, null, false, updatedAt);

        assertThat(service.name()).isEqualTo("payments");
        assertThat(service.description()).isNull();
        assertThat(service.environment()).isEqualTo(ServiceEnvironment.PRODUCTION);
        assertThat(service.active()).isFalse();
        assertThat(service.createdAt()).isEqualTo(createdAt);
        assertThat(service.updatedAt()).isEqualTo(updatedAt);
    }
}
