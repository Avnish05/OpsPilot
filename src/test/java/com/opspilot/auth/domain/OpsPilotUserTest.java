package com.opspilot.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OpsPilotUserTest {
    @Test
    void registrationAlwaysCreatesEnabledEngineerWithNormalizedEmail() {
        OpsPilotUser user = OpsPilotUser.register(UUID.randomUUID(), " Engineer@Example.COM ", "$2a$hash",
                Instant.parse("2026-08-07T00:00:00Z"));

        assertThat(user.email()).isEqualTo("engineer@example.com");
        assertThat(user.role()).isEqualTo(UserRole.ENGINEER);
        assertThat(user.enabled()).isTrue();
    }
}
