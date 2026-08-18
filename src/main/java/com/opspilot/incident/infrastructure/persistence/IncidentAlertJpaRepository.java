package com.opspilot.incident.infrastructure.persistence;
import java.util.List; import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository; interface IncidentAlertJpaRepository extends JpaRepository<IncidentAlertEntity,IncidentAlertEntity.Key>{ List<IncidentAlertEntity> findByIncidentId(UUID incidentId); }
