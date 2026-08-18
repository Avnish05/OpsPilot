package com.opspilot.investigation.application;

import com.opspilot.investigation.api.InvestigationResponse;
import com.opspilot.investigation.api.RecommendedActionResponse;
import com.opspilot.investigation.infrastructure.InvestigationEntity;
import com.opspilot.investigation.infrastructure.InvestigationRepository;
import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class InvestigationPersistenceService {
    private static final Logger log = LoggerFactory.getLogger(InvestigationPersistenceService.class);
    private final InvestigationRepository repository; private final ObjectMapper objectMapper; private final Clock clock;
    public InvestigationPersistenceService(InvestigationRepository repository, ObjectMapper objectMapper, Clock clock) { this.repository = repository; this.objectMapper = objectMapper; this.clock = clock; }

    @Transactional
    public InvestigationResponse save(UUID incidentId, String question, AiInvestigationResult result) {
        try {
            var response = response(repository.save(new InvestigationEntity(UUID.randomUUID(), incidentId, question, result.summary(), result.probableCause(),
                    result.confidence(), objectMapper.writeValueAsString(result.recommendedActions()), objectMapper.writeValueAsString(result.limitations()), clock.instant())));
            log.info("Persisted AI investigation: investigationId={}, incidentId={}", response.id(), incidentId);
            return response;
        } catch (JacksonException exception) { throw new IllegalStateException("Could not serialize investigation", exception); }
    }

    @Transactional(readOnly = true)
    public List<InvestigationResponse> list(UUID incidentId) { return repository.findByIncidentIdOrderByCreatedAtDesc(incidentId).stream().map(this::response).toList(); }
    @Transactional(readOnly = true)
    public InvestigationResponse get(UUID id) { return repository.findById(id).map(this::response).orElseThrow(()->new NoSuchElementException("Investigation " + id + " was not found")); }

    private InvestigationResponse response(InvestigationEntity e) {
        try {
            List<AiInvestigationResult.RecommendedAction> storedActions = objectMapper.readValue(e.recommendedActions(), new TypeReference<>() { });
            var actions = storedActions.stream()
                    .map(a -> new RecommendedActionResponse(a.action(), a.rationale(), a.risk())).toList();
            List<String> limitations = objectMapper.readValue(e.limitations(), new TypeReference<>() { });
            return new InvestigationResponse(e.id(), e.incidentId(), e.question(), e.summary(), e.probableCause(), e.confidence(), actions, limitations, e.createdAt());
        } catch (JacksonException exception) { throw new IllegalStateException("Stored investigation is invalid", exception); }
    }
}
