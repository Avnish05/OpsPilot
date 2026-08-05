package com.opspilot.servicecatalog.api;

import com.opspilot.servicecatalog.application.MonitoredServiceApplicationService;
import com.opspilot.servicecatalog.application.MonitoredServiceQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/services")
public class MonitoredServiceController {
    private final MonitoredServiceApplicationService applicationService;
    private final MonitoredServiceQueryService queryService;
    private final MonitoredServiceApiMapper mapper;

    public MonitoredServiceController(MonitoredServiceApplicationService applicationService,
                                      MonitoredServiceQueryService queryService, MonitoredServiceApiMapper mapper) {
        this.applicationService = applicationService;
        this.queryService = queryService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<MonitoredServiceResponse> create(@Valid @RequestBody CreateMonitoredServiceRequest request) {
        var response = mapper.toResponse(applicationService.create(mapper.toCommand(request)));
        return ResponseEntity.created(URI.create("/api/v1/services/" + response.id())).body(response);
    }

    @GetMapping("/{serviceId}")
    public MonitoredServiceResponse get(@PathVariable UUID serviceId) {
        return mapper.toResponse(queryService.get(serviceId));
    }

    @GetMapping
    public MonitoredServicePageResponse list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                             @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return mapper.toResponse(queryService.list(page, size));
    }

    @PatchMapping("/{serviceId}")
    public MonitoredServiceResponse update(@PathVariable UUID serviceId,
                                           @Valid @RequestBody UpdateMonitoredServiceRequest request) {
        return mapper.toResponse(applicationService.update(serviceId, mapper.toCommand(request)));
    }
}
