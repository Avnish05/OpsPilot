package com.opspilot.incident.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; interface IncidentAlertJpaRepository extends JpaRepository<IncidentAlertEntity,IncidentAlertEntity.Key>{}
