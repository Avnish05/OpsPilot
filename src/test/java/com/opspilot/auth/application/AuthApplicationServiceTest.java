package com.opspilot.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.opspilot.auth.domain.InvalidCredentialsException;
import com.opspilot.auth.domain.OpsPilotUser;
import com.opspilot.auth.domain.OpsPilotUserRepository;
import com.opspilot.auth.domain.UserRole;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {
    @Mock private OpsPilotUserRepository userRepository;
    @Mock private JwtTokenIssuer tokenIssuer;
    private final Clock clock = Clock.fixed(Instant.parse("2026-08-07T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void registersEngineerWithBCryptHash() {
        when(userRepository.existsByEmail("engineer@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var service = new AuthApplicationService(userRepository, new BCryptPasswordEncoder(), tokenIssuer, clock);

        var user = service.register(new RegisterUserCommand("engineer@example.com", "very-secure-password"));

        assertThat(user.role()).isEqualTo(UserRole.ENGINEER);
    }

    @Test
    void rejectsDisabledAccountAtLogin() {
        OpsPilotUser disabled = OpsPilotUser.reconstitute(UUID.randomUUID(), "engineer@example.com",
                new BCryptPasswordEncoder().encode("very-secure-password"), UserRole.ENGINEER, false,
                Instant.now(clock), Instant.now(clock), 0);
        when(userRepository.findByEmail("engineer@example.com")).thenReturn(Optional.of(disabled));
        var service = new AuthApplicationService(userRepository, new BCryptPasswordEncoder(), tokenIssuer, clock);

        assertThatThrownBy(() -> service.login(new LoginCommand("engineer@example.com", "very-secure-password")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
