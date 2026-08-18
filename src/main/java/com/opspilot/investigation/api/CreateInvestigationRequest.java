package com.opspilot.investigation.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInvestigationRequest(@NotBlank @Size(max = 2000) String question) { }
