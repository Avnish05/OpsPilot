package com.opspilot.servicecatalog.domain;

public final class ServiceAlreadyExistsException extends RuntimeException {
    public ServiceAlreadyExistsException(String name, ServiceEnvironment environment) {
        super("A service named '%s' already exists in %s".formatted(name, environment));
    }
}
