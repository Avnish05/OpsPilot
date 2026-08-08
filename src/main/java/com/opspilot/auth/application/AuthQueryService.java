package com.opspilot.auth.application;

import com.opspilot.auth.domain.InvalidCredentialsException;
import com.opspilot.auth.domain.OpsPilotUser;
import com.opspilot.auth.domain.OpsPilotUserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthQueryService {
    private final OpsPilotUserRepository userRepository;

    public AuthQueryService(OpsPilotUserRepository userRepository) { this.userRepository = userRepository; }

    @Transactional(readOnly = true)
    public AuthenticatedUser get(UUID id) {
        OpsPilotUser user = userRepository.findById(id).orElseThrow(InvalidCredentialsException::new);
        return new AuthenticatedUser(user.id(), user.email(), user.role());
    }
}
