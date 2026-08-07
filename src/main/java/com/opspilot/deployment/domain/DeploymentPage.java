package com.opspilot.deployment.domain;

import java.util.List;

public record DeploymentPage(List<Deployment> content, int page, int size, long totalElements, int totalPages) {
}
