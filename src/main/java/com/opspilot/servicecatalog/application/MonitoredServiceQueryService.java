package com.opspilot.servicecatalog.application;

import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServiceNotFoundException;
import com.opspilot.servicecatalog.domain.MonitoredServicePage;
import com.opspilot.servicecatalog.domain.MonitoredServiceRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitoredServiceQueryService {
    private final MonitoredServiceRepository repository;

    public MonitoredServiceQueryService(MonitoredServiceRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public MonitoredService get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new MonitoredServiceNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public MonitoredServicePage list(int page, int size) { return repository.findAll(page, size); }
}
