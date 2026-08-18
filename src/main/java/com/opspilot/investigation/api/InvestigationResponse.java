package com.opspilot.investigation.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvestigationResponse(UUID id, UUID incidentId, String question, String summary, String probableCause,
        double confidence, List<RecommendedActionResponse> recommendedActions, List<String> limitations, Instant createdAt) { }
