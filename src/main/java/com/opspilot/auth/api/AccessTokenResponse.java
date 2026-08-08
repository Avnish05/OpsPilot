package com.opspilot.auth.api;

public record AccessTokenResponse(String accessToken, String tokenType, long expiresIn) { }
