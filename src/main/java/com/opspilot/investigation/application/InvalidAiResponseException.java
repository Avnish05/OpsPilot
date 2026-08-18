package com.opspilot.investigation.application;

public class InvalidAiResponseException extends RuntimeException {
    public InvalidAiResponseException(String message) { super(message); }
    public InvalidAiResponseException(String message, Throwable cause) { super(message, cause); }
}
