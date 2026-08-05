package com.opspilot.servicecatalog.domain;

import java.util.List;

public record MonitoredServicePage(
        List<MonitoredService> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
