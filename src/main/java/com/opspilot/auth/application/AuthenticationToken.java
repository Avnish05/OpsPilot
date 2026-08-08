package com.opspilot.auth.application;

import java.time.Instant;

public record AuthenticationToken(String accessToken, Instant expiresAt) { }
