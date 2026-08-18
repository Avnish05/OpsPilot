package com.opspilot.investigation.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "investigations")
public class InvestigationEntity {
    @Id UUID id;
    @Column(name = "incident_id") UUID incidentId;
    String question;
    String summary;
    @Column(name = "probable_cause") String probableCause;
    double confidence;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "recommended_actions", columnDefinition = "jsonb") String recommendedActions;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") String limitations;
    @Column(name = "created_at") Instant createdAt;
    protected InvestigationEntity() { }
    public InvestigationEntity(UUID id, UUID incidentId, String question, String summary, String probableCause, double confidence,
            String recommendedActions, String limitations, Instant createdAt) {
        this.id = id; this.incidentId = incidentId; this.question = question; this.summary = summary; this.probableCause = probableCause;
        this.confidence = confidence; this.recommendedActions = recommendedActions; this.limitations = limitations; this.createdAt = createdAt;
    }
    public UUID id() { return id; } public UUID incidentId() { return incidentId; } public String question() { return question; }
    public String summary() { return summary; } public String probableCause() { return probableCause; } public double confidence() { return confidence; }
    public String recommendedActions() { return recommendedActions; } public String limitations() { return limitations; } public Instant createdAt() { return createdAt; }
}
