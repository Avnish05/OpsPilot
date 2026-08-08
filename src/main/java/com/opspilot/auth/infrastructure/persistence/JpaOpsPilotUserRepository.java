package com.opspilot.auth.infrastructure.persistence;

import com.opspilot.auth.domain.OpsPilotUser;
import com.opspilot.auth.domain.OpsPilotUserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOpsPilotUserRepository implements OpsPilotUserRepository {
    private final SpringDataOpsPilotUserRepository repository;
    private final OpsPilotUserPersistenceMapper mapper;
    public JpaOpsPilotUserRepository(SpringDataOpsPilotUserRepository repository, OpsPilotUserPersistenceMapper mapper) {
        this.repository=repository; this.mapper=mapper;
    }
    public OpsPilotUser save(OpsPilotUser user) { return mapper.toDomain(repository.save(mapper.toEntity(user))); }
    public Optional<OpsPilotUser> findByEmail(String email) { return repository.findByEmailIgnoreCase(email).map(mapper::toDomain); }
    public Optional<OpsPilotUser> findById(UUID id) { return repository.findById(id).map(mapper::toDomain); }
    public boolean existsByEmail(String email) { return repository.existsByEmailIgnoreCase(email); }
}
