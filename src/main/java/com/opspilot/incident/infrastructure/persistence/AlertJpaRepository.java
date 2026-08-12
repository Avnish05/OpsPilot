package com.opspilot.incident.infrastructure.persistence;
import java.util.UUID; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
interface AlertJpaRepository extends JpaRepository<AlertEntity,UUID>{ Page<AlertEntity> findAll(Pageable pageable); }
