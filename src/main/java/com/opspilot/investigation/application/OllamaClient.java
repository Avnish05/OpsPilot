package com.opspilot.investigation.application;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class OllamaClient {
    private static final Logger log = LoggerFactory.getLogger(OllamaClient.class);
    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final String model;

    public OllamaClient(ObjectMapper objectMapper,
            @Value("${opspilot.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${opspilot.ollama.model:llama3.2}") String model) {
        this.client = RestClient.create(baseUrl); this.objectMapper = objectMapper; this.model = model;
    }

    public AiInvestigationResult investigate(String prompt) {
        log.info("Requesting AI investigation from Ollama: model={}, promptLength={}", model, prompt.length());
        String answer = generate(prompt);
        try { return parse(answer); }
        catch (InvalidAiResponseException invalid) {
            log.warn("Ollama response failed validation; requesting one correction attempt: reason={}", invalid.getMessage());
            return parse(generate("Your previous response was invalid. Return only valid JSON matching the requested schema. Previous response:\n" + answer));
        }
    }

    private String generate(String prompt) {
        try {
            var response = client.post().uri("/api/generate").body(Map.of("model", model, "prompt", prompt, "stream", false,
                    "format", InvestigationResponseSchema.jsonSchema(), "options", Map.of("temperature", 0)))
                    .retrieve().body(OllamaGenerateResponse.class);
            if (response == null || response.response() == null) throw new InvalidAiResponseException("Ollama returned no response");
            log.info("Received response from Ollama: model={}, responseLength={}", model, response.response().length());
            return response.response();
        } catch (RestClientException exception) {
            log.warn("Ollama request failed: model={}", model, exception);
            throw new OllamaUnavailableException(exception);
        }
    }

    private AiInvestigationResult parse(String answer) {
        try {
            ModelOutput output = objectMapper.readValue(answer, ModelOutput.class);
            if (output == null || blank(output.summary()) || blank(output.probableCause()) || output.confidence() == null
                    || output.confidence() < 0 || output.confidence() > 1 || output.recommendedActions() == null || output.limitations() == null) {
                throw new InvalidAiResponseException("AI response does not match the investigation schema");
            }
            var actions = output.recommendedActions().stream().map(action -> {
                if (blank(action.action()) || blank(action.rationale()) || !List.of("LOW", "MEDIUM", "HIGH").contains(action.risk()))
                    throw new InvalidAiResponseException("AI response contains an invalid recommended action");
                return new AiInvestigationResult.RecommendedAction(action.action(), action.rationale(), action.risk());
            }).toList();
            if (output.limitations().stream().anyMatch(this::blank)) throw new InvalidAiResponseException("AI response contains an invalid limitation");
            log.info("Validated Ollama investigation response: actions={}, limitations={}, confidence={}", actions.size(), output.limitations().size(), output.confidence());
            return new AiInvestigationResult(output.summary(), output.probableCause(), output.confidence(), actions, output.limitations());
        } catch (JacksonException exception) {
            log.warn("Ollama response was not valid JSON", exception);
            throw new InvalidAiResponseException("AI response is not valid JSON", exception);
        }
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
    private record OllamaGenerateResponse(String response) { }
    private record ModelOutput(String summary, String probableCause, Double confidence, List<ModelAction> recommendedActions, List<String> limitations) { }
    private record ModelAction(String action, String rationale, String risk) { }
}
