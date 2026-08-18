package com.opspilot.shared.api;

import com.opspilot.servicecatalog.domain.MonitoredServiceNotFoundException;
import com.opspilot.servicecatalog.domain.ServiceAlreadyExistsException;
import com.opspilot.deployment.domain.DeploymentNotFoundException;
import com.opspilot.auth.domain.InvalidCredentialsException;
import com.opspilot.auth.domain.UserAlreadyExistsException;
import com.opspilot.incident.domain.InvalidIncidentStateException;
import com.opspilot.investigation.application.OllamaUnavailableException;
import com.opspilot.investigation.application.InvalidAiResponseException;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({MonitoredServiceNotFoundException.class, DeploymentNotFoundException.class, NoSuchElementException.class})
    ProblemDetail notFound(RuntimeException exception) {
        return problem(HttpStatus.NOT_FOUND, "Resource not found", exception.getMessage());
    }

    @ExceptionHandler({ServiceAlreadyExistsException.class, UserAlreadyExistsException.class, InvalidIncidentStateException.class, DataIntegrityViolationException.class})
    ProblemDetail conflict(Exception exception) {
        return problem(HttpStatus.CONFLICT, "Conflict", exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ProblemDetail badRequest(Exception exception) {
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", "Request validation failed");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail unauthorized(InvalidCredentialsException exception) {
        return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password");
    }

    @ExceptionHandler(OllamaUnavailableException.class)
    ProblemDetail serviceUnavailable(OllamaUnavailableException exception) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "AI service unavailable", "Ollama is unavailable");
    }

    @ExceptionHandler(InvalidAiResponseException.class)
    ProblemDetail invalidAiResponse(InvalidAiResponseException exception) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "AI response unavailable", "Ollama returned an invalid investigation response");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
