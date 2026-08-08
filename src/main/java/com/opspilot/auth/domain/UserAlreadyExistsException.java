package com.opspilot.auth.domain;

public final class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) { super("A user with email '%s' already exists".formatted(email)); }
}
