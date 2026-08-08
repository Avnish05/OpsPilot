package com.opspilot.auth.api;

import com.opspilot.auth.application.AuthApplicationService;
import com.opspilot.auth.application.AuthQueryService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthApplicationService authApplicationService;
    private final AuthQueryService authQueryService;
    private final AuthApiMapper mapper;
    public AuthController(AuthApplicationService authApplicationService, AuthQueryService authQueryService, AuthApiMapper mapper) {
        this.authApplicationService=authApplicationService; this.authQueryService=authQueryService; this.mapper=mapper;
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = mapper.toResponse(authApplicationService.register(mapper.toCommand(request)));
        return ResponseEntity.created(URI.create("/api/v1/auth/users/" + response.id())).body(response);
    }
    @PostMapping("/login")
    public AccessTokenResponse login(@Valid @RequestBody LoginRequest request) {
        return mapper.toResponse(authApplicationService.login(mapper.toCommand(request)));
    }
    @GetMapping("/me")
    public UserResponse me(JwtAuthenticationToken authentication) {
        return mapper.toResponse(authQueryService.get(UUID.fromString(authentication.getToken().getSubject())));
    }
}
