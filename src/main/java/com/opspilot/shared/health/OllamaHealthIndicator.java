package com.opspilot.shared.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component("ollama")
public class OllamaHealthIndicator implements HealthIndicator {
    private final RestClient client;
    public OllamaHealthIndicator(@Value("${opspilot.ollama.base-url}") String baseUrl) { client = RestClient.create(baseUrl); }
    @Override public Health health() {
        try { client.get().uri("/api/tags").retrieve().toBodilessEntity(); return Health.up().build(); }
        catch (RestClientException exception) { return Health.down(exception).build(); }
    }
}
