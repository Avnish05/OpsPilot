package com.opspilot.servicecatalog.infrastructure.persistence;

import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServicePage;
import com.opspilot.servicecatalog.domain.MonitoredServiceRepository;
import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMonitoredServiceRepository implements MonitoredServiceRepository {
    private final SpringDataMonitoredServiceRepository springDataRepository;
    private final MonitoredServicePersistenceMapper mapper;

    public JpaMonitoredServiceRepository(SpringDataMonitoredServiceRepository springDataRepository,
                                         MonitoredServicePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public MonitoredService save(MonitoredService service) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(service)));
    }

    @Override
    public Optional<MonitoredService> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNameAndEnvironment(String name, ServiceEnvironment environment, UUID excludedId) {
        return excludedId == null
                ? springDataRepository.existsByNameIgnoreCaseAndEnvironment(name, environment)
                : springDataRepository.existsByNameIgnoreCaseAndEnvironmentAndIdNot(name, environment, excludedId);
    }

    @Override
    public MonitoredServicePage findAll(int page, int size) {
        var result = springDataRepository.findAll(PageRequest.of(page, size,
                Sort.by("name").ascending().and(Sort.by("id").ascending())));
        return new MonitoredServicePage(result.map(mapper::toDomain).getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
}
