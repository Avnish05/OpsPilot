package com.opspilot.auth.domain;

import java.util.Optional;
import java.util.UUID;

public interface OpsPilotUserRepository {
    OpsPilotUser save(OpsPilotUser user);
    Optional<OpsPilotUser> findByEmail(String email);
    Optional<OpsPilotUser> findById(UUID id);
    boolean existsByEmail(String email);
}
