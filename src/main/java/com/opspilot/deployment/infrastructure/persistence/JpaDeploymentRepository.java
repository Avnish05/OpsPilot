package com.opspilot.deployment.infrastructure.persistence;

import com.opspilot.deployment.domain.Deployment;
import com.opspilot.deployment.domain.DeploymentPage;
import com.opspilot.deployment.domain.DeploymentRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDeploymentRepository implements DeploymentRepository {
    private final SpringDataDeploymentRepository springDataRepository;
    private final DeploymentPersistenceMapper mapper;

    public JpaDeploymentRepository(SpringDataDeploymentRepository springDataRepository, DeploymentPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Deployment save(Deployment deployment) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(deployment)));
    }

    @Override
    public Optional<Deployment> findById(UUID id) { return springDataRepository.findById(id).map(mapper::toDomain); }

    @Override
    public DeploymentPage findByServiceId(UUID serviceId, int page, int size) {
        var result = springDataRepository.findByServiceId(serviceId, PageRequest.of(page, size,
                Sort.by("deployedAt").descending().and(Sort.by("id").descending())));
        return new DeploymentPage(result.map(mapper::toDomain).getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
}
