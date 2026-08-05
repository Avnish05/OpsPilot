package com.opspilot.servicecatalog.application;

import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServiceNotFoundException;
import com.opspilot.servicecatalog.domain.MonitoredServiceRepository;
import com.opspilot.servicecatalog.domain.ServiceAlreadyExistsException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitoredServiceApplicationService {
    private final MonitoredServiceRepository repository;
    private final Clock clock;

    public MonitoredServiceApplicationService(MonitoredServiceRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public MonitoredService create(CreateMonitoredServiceCommand command) {
        if (repository.existsByNameAndEnvironment(command.name(), command.environment(), null)) {
            throw new ServiceAlreadyExistsException(command.name(), command.environment());
        }
        return repository.save(MonitoredService.create(UUID.randomUUID(), command.name(), command.description(),
                command.environment(), command.ownerTeam(), Instant.now(clock)));
    }

    @Transactional
    public MonitoredService update(UUID id, UpdateMonitoredServiceCommand command) {
        MonitoredService service = repository.findById(id).orElseThrow(() -> new MonitoredServiceNotFoundException(id));
        var candidateName = command.name() == null ? service.name() : command.name();
        var candidateEnvironment = command.environment() == null ? service.environment() : command.environment();
        if (repository.existsByNameAndEnvironment(candidateName, candidateEnvironment, id)) {
            throw new ServiceAlreadyExistsException(candidateName, candidateEnvironment);
        }
        service.update(command.name(), command.description(), command.environment(), command.ownerTeam(), command.active(),
                Instant.now(clock));
        return repository.save(service);
    }
}
