package com.opspilot.auth.application;

public interface JwtTokenIssuer {
    AuthenticationToken issue(AuthenticatedUser user);
}
