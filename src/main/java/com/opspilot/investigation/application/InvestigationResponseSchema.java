package com.opspilot.investigation.application;

import java.util.List;
import java.util.Map;

final class InvestigationResponseSchema {
    private InvestigationResponseSchema() { }

    static Map<String, Object> jsonSchema() {
        Map<String, Object> action = Map.of("type", "object", "properties", Map.of(
                "action", Map.of("type", "string"), "rationale", Map.of("type", "string"),
                "risk", Map.of("type", "string", "enum", List.of("LOW", "MEDIUM", "HIGH"))),
                "required", List.of("action", "rationale", "risk"), "additionalProperties", false);
        return Map.of("type", "object", "properties", Map.of(
                "summary", Map.of("type", "string"), "probableCause", Map.of("type", "string"),
                "confidence", Map.of("type", "number", "minimum", 0, "maximum", 1),
                "recommendedActions", Map.of("type", "array", "items", action),
                "limitations", Map.of("type", "array", "items", Map.of("type", "string"))),
                "required", List.of("summary", "probableCause", "confidence", "recommendedActions", "limitations"),
                "additionalProperties", false);
    }
}
