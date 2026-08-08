package com.opspilot.auth.application;

import com.opspilot.auth.domain.InvalidCredentialsException;
import com.opspilot.auth.domain.OpsPilotUser;
import com.opspilot.auth.domain.OpsPilotUserRepository;
import com.opspilot.auth.domain.UserAlreadyExistsException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthApplicationService {
    private final OpsPilotUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenIssuer tokenIssuer;
    private final Clock clock;

    public AuthApplicationService(OpsPilotUserRepository userRepository, PasswordEncoder passwordEncoder,
                                  JwtTokenIssuer tokenIssuer, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenIssuer = tokenIssuer;
        this.clock = clock;
    }

    @Transactional
    public AuthenticatedUser register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) throw new UserAlreadyExistsException(command.email());
        OpsPilotUser user = OpsPilotUser.register(UUID.randomUUID(), command.email(), passwordEncoder.encode(command.password()),
                Instant.now(clock));
        userRepository.save(user);
        return new AuthenticatedUser(user.id(), user.email(), user.role());
    }

    @Transactional(readOnly = true)
    public AuthenticationToken login(LoginCommand command) {
        OpsPilotUser user = userRepository.findByEmail(command.email()).orElseThrow(InvalidCredentialsException::new);
        if (!user.enabled() || !passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return tokenIssuer.issue(new AuthenticatedUser(user.id(), user.email(), user.role()));
    }
}
