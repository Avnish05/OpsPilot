package com.opspilot.auth.api;

import com.opspilot.auth.application.AuthenticatedUser;
import com.opspilot.auth.application.AuthenticationToken;
import com.opspilot.auth.application.LoginCommand;
import com.opspilot.auth.application.RegisterUserCommand;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
class AuthApiMapper {
    RegisterUserCommand toCommand(RegisterRequest request) { return new RegisterUserCommand(request.email(), request.password()); }
    LoginCommand toCommand(LoginRequest request) { return new LoginCommand(request.email(), request.password()); }
    UserResponse toResponse(AuthenticatedUser user) { return new UserResponse(user.id(), user.email(), user.role()); }
    AccessTokenResponse toResponse(AuthenticationToken token) {
        return new AccessTokenResponse(token.accessToken(), "Bearer", Duration.between(Instant.now(), token.expiresAt()).toSeconds());
    }
}
