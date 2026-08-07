package com.opspilot.deployment.api;

import java.util.List;

public record DeploymentPageResponse(List<DeploymentResponse> content, int page, int size, long totalElements,
                                     int totalPages) { }
