package com.opspilot.auth.application;

import com.opspilot.auth.domain.UserRole;
import java.util.UUID;

public record AuthenticatedUser(UUID id, String email, UserRole role) { }
