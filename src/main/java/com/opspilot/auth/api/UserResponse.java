package com.opspilot.auth.api;

import com.opspilot.auth.domain.UserRole;
import java.util.UUID;
public record UserResponse(UUID id, String email, UserRole role) { }
