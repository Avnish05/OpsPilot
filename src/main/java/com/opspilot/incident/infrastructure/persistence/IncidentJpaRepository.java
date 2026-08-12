package com.opspilot.incident.infrastructure.persistence;
import com.opspilot.incident.domain.IncidentStatus; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
interface IncidentJpaRepository extends JpaRepository<IncidentEntity,UUID>{ Optional<IncidentEntity> findFirstByServiceIdAndFingerprintAndStatusNotOrderByCreatedAtDesc(UUID serviceId,String fingerprint,IncidentStatus status); Page<IncidentEntity> findAll(Pageable pageable); }
