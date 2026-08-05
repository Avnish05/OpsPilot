package com.opspilot.servicecatalog.api;

import java.util.List;

public record MonitoredServicePageResponse(List<MonitoredServiceResponse> content, int page, int size,
                                           long totalElements, int totalPages) { }
