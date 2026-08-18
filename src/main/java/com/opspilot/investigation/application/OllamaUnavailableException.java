package com.opspilot.investigation.application;

public class OllamaUnavailableException extends RuntimeException {
    public OllamaUnavailableException(Throwable cause) { super("Ollama is unavailable", cause); }
}
