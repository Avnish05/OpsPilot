package com.opspilot.investigation.application;

import com.opspilot.deployment.application.DeploymentQueryService;
import com.opspilot.incident.infrastructure.persistence.IncidentFacade;
import com.opspilot.investigation.api.InvestigationResponse;
import com.opspilot.servicecatalog.application.MonitoredServiceQueryService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.opspilot.shared.observability.OpsPilotMetrics;

@Service
public class InvestigationApplicationService {
    private static final Logger log = LoggerFactory.getLogger(InvestigationApplicationService.class);
    private final IncidentFacade incidents; private final MonitoredServiceQueryService services; private final DeploymentQueryService deployments;
    private final OllamaClient ollama; private final InvestigationPersistenceService persistence; private final ObjectMapper objectMapper;
    private final OpsPilotMetrics metrics;
    public InvestigationApplicationService(IncidentFacade incidents, MonitoredServiceQueryService services, DeploymentQueryService deployments,
            OllamaClient ollama, InvestigationPersistenceService persistence, ObjectMapper objectMapper, OpsPilotMetrics metrics) {
        this.incidents = incidents; this.services = services; this.deployments = deployments; this.ollama = ollama; this.persistence = persistence; this.objectMapper = objectMapper; this.metrics = metrics;
    }

    public InvestigationResponse create(UUID incidentId, String question) {
        log.info("Starting AI investigation: incidentId={}, questionLength={}", incidentId, question.length());
        var context = incidents.investigationContext(incidentId);
        var service = services.get(context.incident().serviceId());
        var recentDeployments = deployments.listForService(service.id(), 0, 10).content();
        log.info("Built trusted AI investigation context: incidentId={}, alerts={}, deployments={}", incidentId, context.alerts().size(), recentDeployments.size());
        AiInvestigationResult result; var timer = metrics.startAiTimer();
        try { result = ollama.investigate(prompt(question, context, service, recentDeployments)); metrics.aiInvestigation(); }
        catch (RuntimeException exception) { metrics.aiFailure(); throw exception; }
        finally { metrics.recordAiDuration(timer); }
        var investigation = persistence.save(incidentId, question, result);
        log.info("Saved AI investigation: investigationId={}, incidentId={}", investigation.id(), incidentId);
        return investigation;
    }
    public List<InvestigationResponse> list(UUID incidentId) { incidents.incident(incidentId); return persistence.list(incidentId); }
    public InvestigationResponse get(UUID id) { return persistence.get(id); }

    private String prompt(String question, IncidentFacade.InvestigationContext incident, Object service, Object recentDeployments) {
        try {
            String context = objectMapper.writeValueAsString(Map.of("incident", incident.incident(), "alerts", incident.alerts(), "service", service, "recentDeployments", recentDeployments, "question", question));
            return "You are an operations-investigation assistant. Use only this trusted context. Do not claim actions were performed. "
                    + "Return only JSON with summary, probableCause, confidence (0 to 1), recommendedActions (array of action, rationale, risk where risk is LOW, MEDIUM, or HIGH), and limitations (array of strings). Context:\n" + context;
        } catch (JacksonException exception) { throw new IllegalStateException("Could not build AI context", exception); }
    }
}
