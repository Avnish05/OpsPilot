package com.opspilot.investigation.application;

import java.util.List;

public record AiInvestigationResult(String summary, String probableCause, double confidence,
        List<RecommendedAction> recommendedActions, List<String> limitations) {
    public record RecommendedAction(String action, String rationale, String risk) { }
}
