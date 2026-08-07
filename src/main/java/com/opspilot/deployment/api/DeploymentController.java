package com.opspilot.deployment.api;

import com.opspilot.deployment.application.DeploymentApplicationService;
import com.opspilot.deployment.application.DeploymentQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
public class DeploymentController {
    private final DeploymentApplicationService applicationService;
    private final DeploymentQueryService queryService;
    private final DeploymentApiMapper mapper;

    public DeploymentController(DeploymentApplicationService applicationService, DeploymentQueryService queryService,
                                DeploymentApiMapper mapper) {
        this.applicationService = applicationService;
        this.queryService = queryService;
        this.mapper = mapper;
    }

    @PostMapping("/services/{serviceId}/deployments")
    public ResponseEntity<DeploymentResponse> create(@PathVariable UUID serviceId,
                                                      @Valid @RequestBody CreateDeploymentRequest request) {
        var response = mapper.toResponse(applicationService.create(serviceId, mapper.toCommand(request)));
        return ResponseEntity.created(URI.create("/api/v1/deployments/" + response.id())).body(response);
    }

    @GetMapping("/services/{serviceId}/deployments")
    public DeploymentPageResponse listForService(@PathVariable UUID serviceId,
                                                  @RequestParam(defaultValue = "0") @Min(0) int page,
                                                  @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return mapper.toResponse(queryService.listForService(serviceId, page, size));
    }

    @GetMapping("/deployments/{deploymentId}")
    public DeploymentResponse get(@PathVariable UUID deploymentId) {
        return mapper.toResponse(queryService.get(deploymentId));
    }
}
