package com.opspilot.investigation.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestigationRepository extends JpaRepository<InvestigationEntity, UUID> {
    List<InvestigationEntity> findByIncidentIdOrderByCreatedAtDesc(UUID incidentId);
}
