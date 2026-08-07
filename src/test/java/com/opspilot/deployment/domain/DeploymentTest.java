package com.opspilot.deployment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeploymentTest {
    @Test
    void createsDeploymentWithSuppliedDetails() {
        Instant now = Instant.parse("2026-08-07T00:00:00Z");
        Deployment deployment = Deployment.create(UUID.randomUUID(), UUID.randomUUID(), " 2.4.1 ",
                DeploymentStatus.SUCCEEDED, now.minusSeconds(60), now);

        assertThat(deployment.releaseVersion()).isEqualTo("2.4.1");
        assertThat(deployment.status()).isEqualTo(DeploymentStatus.SUCCEEDED);
        assertThat(deployment.createdAt()).isEqualTo(now);
    }

    @Test
    void rejectsBlankReleaseVersion() {
        Instant now = Instant.parse("2026-08-07T00:00:00Z");
        assertThatThrownBy(() -> Deployment.create(UUID.randomUUID(), UUID.randomUUID(), " ",
                DeploymentStatus.STARTED, now, now)).isInstanceOf(IllegalArgumentException.class);
    }
}
